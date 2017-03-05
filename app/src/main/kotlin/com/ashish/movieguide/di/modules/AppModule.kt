package com.ashish.movieguide.di.modules

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.ashish.movieguide.data.database.DatabaseHelper
import com.ashish.movieguide.data.database.entities.CreditEntity
import com.ashish.movieguide.data.database.entities.CreditEntitySQLiteTypeMapping
import com.ashish.movieguide.data.database.entities.GenreEntity
import com.ashish.movieguide.data.database.entities.GenreEntitySQLiteTypeMapping
import com.ashish.movieguide.data.database.entities.ImageEntity
import com.ashish.movieguide.data.database.entities.ImageEntitySQLiteTypeMapping
import com.ashish.movieguide.data.database.entities.MovieDetailEntity
import com.ashish.movieguide.data.database.entities.MovieEntity
import com.ashish.movieguide.data.database.entities.VideoEntity
import com.ashish.movieguide.data.database.entities.VideoEntitySQLiteTypeMapping
import com.ashish.movieguide.data.database.resolvers.movie.MovieSQLiteTypeMapping
import com.ashish.movieguide.data.database.resolvers.moviedetail.MovieDetailSQLiteTypeMapping
import com.ashish.movieguide.di.qualifiers.ApplicationContext
import com.ashish.movieguide.utils.extensions.defaultSharedPreferences
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Ashish on Jan 02.
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    @ApplicationContext
    fun provideAppContext(): Context = application

    @Provides
    @Singleton
    fun provideSQLiteOperHelper(@ApplicationContext context: Context): SQLiteOpenHelper = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) = context.defaultSharedPreferences

    @Provides
    @Singleton
    fun provideStorIOSQlite(sqLiteOpenHelper: SQLiteOpenHelper): StorIOSQLite {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(MovieEntity::class.java, MovieSQLiteTypeMapping())
                .addTypeMapping(GenreEntity::class.java, GenreEntitySQLiteTypeMapping())
                .addTypeMapping(ImageEntity::class.java, ImageEntitySQLiteTypeMapping())
                .addTypeMapping(VideoEntity::class.java, VideoEntitySQLiteTypeMapping())
                .addTypeMapping(CreditEntity::class.java, CreditEntitySQLiteTypeMapping())
                .addTypeMapping(MovieDetailEntity::class.java, MovieDetailSQLiteTypeMapping())
                .build()
    }
}