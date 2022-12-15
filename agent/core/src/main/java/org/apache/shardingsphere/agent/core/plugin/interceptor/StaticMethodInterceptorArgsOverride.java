/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.agent.core.plugin.interceptor;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.shardingsphere.agent.core.plugin.advice.StaticMethodAroundAdvice;
import org.apache.shardingsphere.agent.core.plugin.OverrideArgsInvoker;
import org.apache.shardingsphere.agent.core.plugin.MethodInvocationResult;
import org.apache.shardingsphere.agent.core.logging.LoggerFactory;
import org.apache.shardingsphere.agent.core.plugin.PluginContext;

import java.lang.reflect.Method;

/**
 * Proxy class for ByteBuddy to intercept methods of target and weave pre- and post-method around the target method with args override.
 */
@RequiredArgsConstructor
public class StaticMethodInterceptorArgsOverride {
    
    private static final LoggerFactory.Logger LOGGER = LoggerFactory.getLogger(StaticMethodInterceptorArgsOverride.class);
    
    private final StaticMethodAroundAdvice staticMethodAroundAdvice;
    
    /**
     * Only intercept static method.
     *
     * @param klass the class of target
     * @param method the intercepted method
     * @param args the all arguments of method
     * @param callable the origin method invocation
     * @return the return value of target invocation
     */
    @RuntimeType
    public Object intercept(@Origin final Class<?> klass, @Origin final Method method, @AllArguments final Object[] args, @Morph final OverrideArgsInvoker callable) {
        MethodInvocationResult methodResult = new MethodInvocationResult();
        Object result;
        boolean adviceEnabled = staticMethodAroundAdvice.disableCheck() || PluginContext.isPluginEnabled();
        try {
            if (adviceEnabled) {
                staticMethodAroundAdvice.beforeMethod(klass, method, args, methodResult);
            }
            // CHECKSTYLE:OFF
        } catch (final Throwable ex) {
            // CHECKSTYLE:ON
            LOGGER.error("Failed to execute the pre-method of method[{}] in class[{}]", method.getName(), klass, ex);
        }
        try {
            if (methodResult.isRebased()) {
                result = methodResult.getResult();
            } else {
                result = callable.call(args);
            }
            methodResult.rebase(result);
            // CHECKSTYLE:OFF
        } catch (final Throwable ex) {
            // CHECKSTYLE:ON
            try {
                if (adviceEnabled) {
                    staticMethodAroundAdvice.onThrowing(klass, method, args, ex);
                }
                // CHECKSTYLE:OFF
            } catch (final Throwable ignored) {
                // CHECKSTYLE:ON
                LOGGER.error("Failed to execute the error handler of method[{}] in class[{}]", method.getName(), klass, ex);
            }
            throw ex;
        } finally {
            try {
                if (adviceEnabled) {
                    staticMethodAroundAdvice.afterMethod(klass, method, args, methodResult);
                }
                // CHECKSTYLE:OFF
            } catch (final Throwable ex) {
                // CHECKSTYLE:ON
                LOGGER.error("Failed to execute the post-method of method[{}] in class[{}]", method.getName(), klass, ex);
            }
        }
        return methodResult.isRebased() ? methodResult.getResult() : result;
    }
}