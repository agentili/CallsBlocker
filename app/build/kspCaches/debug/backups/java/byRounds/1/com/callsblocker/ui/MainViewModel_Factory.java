package com.callsblocker.ui;

import android.content.Context;
import com.callsblocker.data.BlockedEntryRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<BlockedEntryRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public MainViewModel_Factory(Provider<BlockedEntryRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static MainViewModel_Factory create(
      javax.inject.Provider<BlockedEntryRepository> repositoryProvider,
      javax.inject.Provider<Context> contextProvider) {
    return new MainViewModel_Factory(Providers.asDaggerProvider(repositoryProvider), Providers.asDaggerProvider(contextProvider));
  }

  public static MainViewModel_Factory create(Provider<BlockedEntryRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    return new MainViewModel_Factory(repositoryProvider, contextProvider);
  }

  public static MainViewModel newInstance(BlockedEntryRepository repository, Context context) {
    return new MainViewModel(repository, context);
  }
}
