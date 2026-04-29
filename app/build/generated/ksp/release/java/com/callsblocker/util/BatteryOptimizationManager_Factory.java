package com.callsblocker.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class BatteryOptimizationManager_Factory implements Factory<BatteryOptimizationManager> {
  private final Provider<Context> contextProvider;

  public BatteryOptimizationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BatteryOptimizationManager get() {
    return newInstance(contextProvider.get());
  }

  public static BatteryOptimizationManager_Factory create(
      javax.inject.Provider<Context> contextProvider) {
    return new BatteryOptimizationManager_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static BatteryOptimizationManager_Factory create(Provider<Context> contextProvider) {
    return new BatteryOptimizationManager_Factory(contextProvider);
  }

  public static BatteryOptimizationManager newInstance(Context context) {
    return new BatteryOptimizationManager(context);
  }
}
