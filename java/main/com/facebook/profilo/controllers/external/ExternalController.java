/**
 * Copyright 2004-present, Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.profilo.controllers.external;

import static com.facebook.profilo.core.ProfiloConstants.CPU_SAMPLING_RATE_CONFIG_PARAM;

import com.facebook.profilo.config.ControllerConfig;
import com.facebook.profilo.core.TraceController;
import com.facebook.profilo.core.TriggerRegistry;
import com.facebook.profilo.ipc.TraceContext;
import java.util.TreeMap;
import javax.annotation.Nullable;

public class ExternalController implements TraceController {

  public static final int TRIGGER_EXTERNAL = TriggerRegistry.newTrigger("external");

  public static class Config {

    public int providers;
    public int cpuSamplingRateMs;
  }

  @Override
  public int evaluateConfig(long longContext, @Nullable Object context, ControllerConfig ignored) {
    return getConfigFromContext(context).providers;
  }

  @Override
  public TraceContext.ProviderExtras getProviderExtras(
      long longContext, @Nullable Object context, ControllerConfig config) {
    if (context == null) {
      return TraceContext.ProviderExtras.EMPTY;
    }
    TreeMap<String, Integer> extraIntMap = new TreeMap<>();
    extraIntMap.put(
        CPU_SAMPLING_RATE_CONFIG_PARAM, getConfigFromContext(context).cpuSamplingRateMs);
    return new TraceContext.ProviderExtras(extraIntMap, null, null);
  }

  private Config getConfigFromContext(Object context) {
    if (!(context instanceof Config)) {
      throw new RuntimeException(
          "Context for ExternalController must " + "be an instance of ExternalController.Config");
    }
    return (Config) context;
  }

  @Override
  public boolean contextsEqual(
      long fstLong, @Nullable Object fst, long sndLong, @Nullable Object snd) {
    // Always honor stop requests.

    return true;
  }

  @Override
  public boolean isConfigurable() {
    return false;
  }
}
