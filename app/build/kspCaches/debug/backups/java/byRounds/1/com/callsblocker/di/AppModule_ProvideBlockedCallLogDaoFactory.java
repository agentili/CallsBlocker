package com.callsblocker.di;

import com.callsblocker.data.AppDatabase;
import com.callsblocker.data.BlockedCallLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideBlockedCallLogDaoFactory implements Factory<BlockedCallLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideBlockedCallLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BlockedCallLogDao get() {
    return provideBlockedCallLogDao(dbProvider.get());
  }

  public static AppModule_ProvideBlockedCallLogDaoFactory create(
      javax.inject.Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBlockedCallLogDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static AppModule_ProvideBlockedCallLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBlockedCallLogDaoFactory(dbProvider);
  }

  public static BlockedCallLogDao provideBlockedCallLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBlockedCallLogDao(db));
  }
}
