package com.callsblocker.ui;

import android.content.Context;
import com.callsblocker.util.PrefsManager;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<PrefsManager> prefsManagerProvider;

  private final Provider<Context> contextProvider;

  public OnboardingViewModel_Factory(Provider<PrefsManager> prefsManagerProvider,
      Provider<Context> contextProvider) {
    this.prefsManagerProvider = prefsManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(prefsManagerProvider.get(), contextProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      javax.inject.Provider<PrefsManager> prefsManagerProvider,
      javax.inject.Provider<Context> contextProvider) {
    return new OnboardingViewModel_Factory(Providers.asDaggerProvider(prefsManagerProvider), Providers.asDaggerProvider(contextProvider));
  }

  public static OnboardingViewModel_Factory create(Provider<PrefsManager> prefsManagerProvider,
      Provider<Context> contextProvider) {
    return new OnboardingViewModel_Factory(prefsManagerProvider, contextProvider);
  }

  public static OnboardingViewModel newInstance(PrefsManager prefsManager, Context context) {
    return new OnboardingViewModel(prefsManager, context);
  }
}
