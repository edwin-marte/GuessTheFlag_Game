package com.em.guesstheflag.application.injection

import com.em.guesstheflag.domain.RepositoryImpl
import com.em.guesstheflag.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindIRepository(repositoryImpl: RepositoryImpl): Repository
}