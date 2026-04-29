package com.callsblocker.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class BlockedEntryRepository_Factory implements Factory<BlockedEntryRepository> {
  private final Provider<BlockedEntryDao> blockedEntryDaoProvider;

  private final Provider<BlockedCallLogDao> blockedCallLogDaoProvider;

  public BlockedEntryRepository_Factory(Provider<BlockedEntryDao> blockedEntryDaoProvider,
      Provider<BlockedCallLogDao> blockedCallLogDaoProvider) {
    this.blockedEntryDaoProvider = blockedEntryDaoProvider;
    this.blockedCallLogDaoProvider = blockedCallLogDaoProvider;
  }

  @Override
  public BlockedEntryRepository get() {
    return newInstance(blockedEntryDaoProvider.get(), blockedCallLogDaoProvider.get());
  }

  public static BlockedEntryRepository_Factory create(
      javax.inject.Provider<BlockedEntryDao> blockedEntryDaoProvider,
      javax.inject.Provider<BlockedCallLogDao> blockedCallLogDaoProvider) {
    return new BlockedEntryRepository_Factory(Providers.asDaggerProvider(blockedEntryDaoProvider), Providers.asDaggerProvider(blockedCallLogDaoProvider));
  }

  public static BlockedEntryRepository_Factory create(
      Provider<BlockedEntryDao> blockedEntryDaoProvider,
      Provider<BlockedCallLogDao> blockedCallLogDaoProvider) {
    return new BlockedEntryRepository_Factory(blockedEntryDaoProvider, blockedCallLogDaoProvider);
  }

  public static BlockedEntryRepository newInstance(BlockedEntryDao blockedEntryDao,
      BlockedCallLogDao blockedCallLogDao) {
    return new BlockedEntryRepository(blockedEntryDao, blockedCallLogDao);
  }
}
