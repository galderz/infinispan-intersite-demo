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
import org.infinispan.container.entries.ImmortalCacheEntry;
import org.infinispan.marshall.Marshaller;
import org.infinispan.marshall.VersionAwareMarshaller;
import org.infinispan.marshall.jboss.GenericJBossMarshaller;
import org.infinispan.server.core.CacheValue;
import org.infinispan.util.ByteArrayKey;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class StandbyTestServlet extends HttpServlet
{
   @Resource(name = "name") String name;

   @Inject
   StandbyCacheContainerBean standbyCacheContainerBean;

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String key = "counter";
      Cache cache = standbyCacheContainerBean.getCache();
      Integer counter = cacheGet(cache, key);
      if (counter != null) {
         Integer newCounter = counter.intValue() + 1;
         cachePut(cache, key, newCounter);
         response.getWriter().append(newCounter.toString());
      }
   }
   
   private Integer cacheGet(Cache cache, String key) throws IOException {
      ByteArrayKey cacheKey = marshallKey(key);
      CacheValue cacheValue = (CacheValue) cache.get(cacheKey);
      byte[] value = cacheValue.data();
      try {
         // RemoteCacheStore stores ImmortalCacheEntry, not just the value
         ImmortalCacheEntry entry = (ImmortalCacheEntry) standbyCacheContainerBean.getMarshaller().objectFromByteBuffer(value);
         return (Integer) entry.getValue();
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   private void cachePut(Cache cache, String key, Integer value) throws IOException {
      ByteArrayKey cacheKey = marshallKey(key);
      CacheValue cacheValue = (CacheValue) cache.get(cacheKey);
      ImmortalCacheEntry entry = null;
      try {
         entry = (ImmortalCacheEntry) standbyCacheContainerBean.getMarshaller().objectFromByteBuffer(cacheValue.data());
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }

      entry.setValue(value);
      CacheValue newCacheValue = new CacheValue(marshallValue(entry), cacheValue.version() + 1);
      cache.put(cacheKey, newCacheValue);
   }

   private ByteArrayKey marshallKey(String key) throws IOException {
      byte[] keyBytes = standbyCacheContainerBean.getMarshaller().objectToByteBuffer(key);
      return new ByteArrayKey(keyBytes);
   }

   private byte[] marshallValue(ImmortalCacheEntry entry) throws IOException {
      return standbyCacheContainerBean.getMarshaller().objectToByteBuffer(entry);
   }
}
