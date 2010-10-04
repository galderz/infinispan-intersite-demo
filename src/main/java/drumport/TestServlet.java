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
package drumport;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * TestServlet
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class TestServlet extends HttpServlet
{
   @Inject
   CacheContainerBean cacheContainerBean;

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String key = "counter";
      Cache<String, Integer> cache = cacheContainerBean.getCache();
      Integer counter = cache.get(key);
      Integer newCounter;
      if (counter != null)
         newCounter = counter.intValue() + 1;
      else
         newCounter = 1;

      cache.put(key, newCounter);
      response.getWriter().append(newCounter.toString());

//      if (counter != null) {
//         Integer newCounter = counter.intValue() + 1;
//         boolean replaced = cache.replace(key, counter, newCounter);
//         if (replaced)
//            response.getWriter().append(newCounter.toString());
//         else
//            response.getWriter().append("Concurrent counter update, retry");
//      } else {
//         Integer newCounter = 1;
//         Integer prev = cache.putIfAbsent(key, newCounter);
//         if (prev != null)
//            response.getWriter().append("Concurrent counter creation, retry");
//         else
//            response.getWriter().append(newCounter.toString());
//      }
   }
}
