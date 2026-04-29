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
public final class AppRoleManager_Factory implements Factory<AppRoleManager> {
  private final Provider<Context> contextProvider;

  public AppRoleManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppRoleManager get() {
    return newInstance(contextProvider.get());
  }

  public static AppRoleManager_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new AppRoleManager_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static AppRoleManager_Factory create(Provider<Context> contextProvider) {
    return new AppRoleManager_Factory(contextProvider);
  }

  public static AppRoleManager newInstance(Context context) {
    return new AppRoleManager(context);
  }
}
