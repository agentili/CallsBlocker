package com.callsblocker.ui;

import com.callsblocker.util.AppRoleManager;
import com.callsblocker.util.BatteryOptimizationManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

@QualifierMetadata
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AppRoleManager> roleManagerProvider;

  private final Provider<BatteryOptimizationManager> batteryManagerProvider;

  public MainActivity_MembersInjector(Provider<AppRoleManager> roleManagerProvider,
      Provider<BatteryOptimizationManager> batteryManagerProvider) {
    this.roleManagerProvider = roleManagerProvider;
    this.batteryManagerProvider = batteryManagerProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<AppRoleManager> roleManagerProvider,
      Provider<BatteryOptimizationManager> batteryManagerProvider) {
    return new MainActivity_MembersInjector(roleManagerProvider, batteryManagerProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<AppRoleManager> roleManagerProvider,
      javax.inject.Provider<BatteryOptimizationManager> batteryManagerProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(roleManagerProvider), Providers.asDaggerProvider(batteryManagerProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectRoleManager(instance, roleManagerProvider.get());
    injectBatteryManager(instance, batteryManagerProvider.get());
  }

  @InjectedFieldSignature("com.callsblocker.ui.MainActivity.roleManager")
  public static void injectRoleManager(MainActivity instance, AppRoleManager roleManager) {
    instance.roleManager = roleManager;
  }

  @InjectedFieldSignature("com.callsblocker.ui.MainActivity.batteryManager")
  public static void injectBatteryManager(MainActivity instance,
      BatteryOptimizationManager batteryManager) {
    instance.batteryManager = batteryManager;
  }
}
