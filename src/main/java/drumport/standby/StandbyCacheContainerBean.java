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

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.marshall.Marshaller;
import org.infinispan.marshall.VersionAwareMarshaller;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Galder Zamarreño
 */
@ApplicationScoped
public class StandbyCacheContainerBean
{
   private EmbeddedCacheManager cacheManager;

   // We need version aware marshaller cos that's what RemoteCacheStore uses to marshall stuff
   private VersionAwareMarshaller marshaller;

   @PostConstruct
   public void init() throws IOException {
      cacheManager = StandbyContainerTestCase.cacheManager;
      marshaller = new VersionAwareMarshaller();
      marshaller.inject(Thread.currentThread().getContextClassLoader(), null);
      marshaller.start();
   }

   public Cache getCache() {
      return cacheManager.getCache();
   }

   public Marshaller getMarshaller() {
      return marshaller;
   }
}
