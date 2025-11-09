package com.semilladigital.courses.di

import com.semilladigital.courses.data.repository.CourseRepositoryImpl
import com.semilladigital.courses.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository
}