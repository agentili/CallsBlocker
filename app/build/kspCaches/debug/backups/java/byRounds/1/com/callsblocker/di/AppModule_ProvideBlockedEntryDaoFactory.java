package com.callsblocker.di;

import com.callsblocker.data.AppDatabase;
import com.callsblocker.data.BlockedEntryDao;
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
public final class AppModule_ProvideBlockedEntryDaoFactory implements Factory<BlockedEntryDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideBlockedEntryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BlockedEntryDao get() {
    return provideBlockedEntryDao(dbProvider.get());
  }

  public static AppModule_ProvideBlockedEntryDaoFactory create(
      javax.inject.Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBlockedEntryDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static AppModule_ProvideBlockedEntryDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBlockedEntryDaoFactory(dbProvider);
  }

  public static BlockedEntryDao provideBlockedEntryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBlockedEntryDao(db));
  }
}
