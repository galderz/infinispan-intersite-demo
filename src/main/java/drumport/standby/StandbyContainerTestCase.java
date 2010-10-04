/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package drumport.standby;

import drumport.CacheContainerBean;
import drumport.MavenArtifactResolver;
import drumport.TestServlet;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Galder Zamarreño
 */
@RunWith(Arquillian.class)
public class StandbyContainerTestCase {

   @Deployment
   public static WebArchive createTestArchive() {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClasses(StandbyTestServlet.class, StandbyCacheContainerBean.class)
            .addLibrary(MavenArtifactResolver.resolve("org.jboss.weld.servlet:weld-servlet:1.1.0.Beta1"))
            .addLibrary(MavenArtifactResolver.resolve("log4j:log4j:1.2.16"))
            .addWebResource(new ByteArrayAsset(new byte[0]), "beans.xml")
            .addResource("standby-in-container-context.xml", "META-INF/context.xml")
            .setWebXML("standby-in-container-web.xml");
   }

   public static EmbeddedCacheManager cacheManager;

   @Test
   public void start() throws Exception {
      cacheManager = new DefaultCacheManager("standby-infinispan.xml");
      startHotRodServer();
      
      while (true) {
         Thread.sleep(2000);
      }
   }

   private void startHotRodServer() {
      HotRodServer server = new HotRodServer();
      server.start(getProperties(), cacheManager);
   }

   private Properties getProperties() {
      Properties p = new Properties();
      p.setProperty("infinispan.server.host", "127.0.0.4");
      p.setProperty("infinispan.server.port", "11311");
      return p;
   }
   
}
