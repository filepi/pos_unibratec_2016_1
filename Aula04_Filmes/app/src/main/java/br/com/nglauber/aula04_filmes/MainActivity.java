package br.com.nglauber.aula04_filmes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import br.com.nglauber.aula04_filmes.model.Movie;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getBoolean(R.bool.phone)) {
            MoviesPagerAdapter pagerAdapter = new MoviesPagerAdapter(getSupportFragmentManager());

            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            MovieListFragment movieListFragment = (MovieListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragmentList);
            movieListFragment.setMovieClickListener(mMovieClickListener);
        }
    }

    private OnMovieClickListener mMovieClickListener = new OnMovieClickListener() {
        @Override
        public void onMovieClick(Movie movie, int position) {
            if (getResources().getBoolean(R.bool.phone)) {
                // Phone
                Intent it = new Intent(MainActivity.this, DetailActivity.class);
                it.putExtra(DetailActivity.EXTRA_ID, movie.getId());
                startActivity(it);
            } else {
                // Tablet
                DetailMovieFragment detailMovieFragment =
                        DetailMovieFragment.newInstance(movie.getId());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholderDetail, detailMovieFragment)
                        .commit();
            }
        }
    };

    class MoviesPagerAdapter extends FragmentPagerAdapter {
        public MoviesPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                MovieListFragment movieListFragment = new MovieListFragment();
                movieListFragment.setMovieClickListener(mMovieClickListener);
                return movieListFragment;
            }
            else return new FavoriteMoviesFragment();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return getString(R.string.tab_search);
            else return getString(R.string.tab_favorites);
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}
