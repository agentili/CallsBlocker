package com.callsblocker.service;

import com.callsblocker.data.BlockedEntryRepository;
import com.callsblocker.util.NotificationHelper;
import com.callsblocker.util.PrefsManager;
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
public final class CallBlockerService_MembersInjector implements MembersInjector<CallBlockerService> {
  private final Provider<BlockedEntryRepository> repositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  private final Provider<PrefsManager> prefsManagerProvider;

  public CallBlockerService_MembersInjector(Provider<BlockedEntryRepository> repositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<PrefsManager> prefsManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
    this.prefsManagerProvider = prefsManagerProvider;
  }

  public static MembersInjector<CallBlockerService> create(
      Provider<BlockedEntryRepository> repositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<PrefsManager> prefsManagerProvider) {
    return new CallBlockerService_MembersInjector(repositoryProvider, notificationHelperProvider, prefsManagerProvider);
  }

  public static MembersInjector<CallBlockerService> create(
      javax.inject.Provider<BlockedEntryRepository> repositoryProvider,
      javax.inject.Provider<NotificationHelper> notificationHelperProvider,
      javax.inject.Provider<PrefsManager> prefsManagerProvider) {
    return new CallBlockerService_MembersInjector(Providers.asDaggerProvider(repositoryProvider), Providers.asDaggerProvider(notificationHelperProvider), Providers.asDaggerProvider(prefsManagerProvider));
  }

  @Override
  public void injectMembers(CallBlockerService instance) {
    injectRepository(instance, repositoryProvider.get());
    injectNotificationHelper(instance, notificationHelperProvider.get());
    injectPrefsManager(instance, prefsManagerProvider.get());
  }

  @InjectedFieldSignature("com.callsblocker.service.CallBlockerService.repository")
  public static void injectRepository(CallBlockerService instance,
      BlockedEntryRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.callsblocker.service.CallBlockerService.notificationHelper")
  public static void injectNotificationHelper(CallBlockerService instance,
      NotificationHelper notificationHelper) {
    instance.notificationHelper = notificationHelper;
  }

  @InjectedFieldSignature("com.callsblocker.service.CallBlockerService.prefsManager")
  public static void injectPrefsManager(CallBlockerService instance, PrefsManager prefsManager) {
    instance.prefsManager = prefsManager;
  }
}
