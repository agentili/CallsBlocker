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
public final class OnboardingActivity_MembersInjector implements MembersInjector<OnboardingActivity> {
  private final Provider<AppRoleManager> roleManagerProvider;

  private final Provider<BatteryOptimizationManager> batteryManagerProvider;

  public OnboardingActivity_MembersInjector(Provider<AppRoleManager> roleManagerProvider,
      Provider<BatteryOptimizationManager> batteryManagerProvider) {
    this.roleManagerProvider = roleManagerProvider;
    this.batteryManagerProvider = batteryManagerProvider;
  }

  public static MembersInjector<OnboardingActivity> create(
      Provider<AppRoleManager> roleManagerProvider,
      Provider<BatteryOptimizationManager> batteryManagerProvider) {
    return new OnboardingActivity_MembersInjector(roleManagerProvider, batteryManagerProvider);
  }

  public static MembersInjector<OnboardingActivity> create(
      javax.inject.Provider<AppRoleManager> roleManagerProvider,
      javax.inject.Provider<BatteryOptimizationManager> batteryManagerProvider) {
    return new OnboardingActivity_MembersInjector(Providers.asDaggerProvider(roleManagerProvider), Providers.asDaggerProvider(batteryManagerProvider));
  }

  @Override
  public void injectMembers(OnboardingActivity instance) {
    injectRoleManager(instance, roleManagerProvider.get());
    injectBatteryManager(instance, batteryManagerProvider.get());
  }

  @InjectedFieldSignature("com.callsblocker.ui.OnboardingActivity.roleManager")
  public static void injectRoleManager(OnboardingActivity instance, AppRoleManager roleManager) {
    instance.roleManager = roleManager;
  }

  @InjectedFieldSignature("com.callsblocker.ui.OnboardingActivity.batteryManager")
  public static void injectBatteryManager(OnboardingActivity instance,
      BatteryOptimizationManager batteryManager) {
    instance.batteryManager = batteryManager;
  }
}
