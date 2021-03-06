package br.com.nglauber.aula04_filmes;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.nglauber.aula04_filmes.http.MoviesSearchTask;
import br.com.nglauber.aula04_filmes.model.Movie;


public class MovieListFragment extends Fragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Movie>>
{
    private static final String QUERY_PARAM = "param";
    public static final int LOADER_ID = 0;

    RecyclerView mRecyclerView;
    MovieAdapter mAdapter;
    List<Movie> mMoviesList;
    LoaderManager mLoaderManager;
    OnMovieClickListener mMovieClickListener;

    public MovieListFragment() {
    }

    public void setMovieClickListener(OnMovieClickListener mMovieClickListener) {
        this.mMovieClickListener = mMovieClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMoviesList = new ArrayList<>();
        mAdapter = new MovieAdapter(getActivity(), mMoviesList);
        mAdapter.setMovieClickListener(new OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie, int position) {
                if (mMovieClickListener != null){
                    mMovieClickListener.onMovieClick(movie, position);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.main_recycler_movies);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        mRecyclerView.setAdapter(mAdapter);

        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    // ---- OnQueryTextListener
    @Override
    public boolean onQueryTextSubmit(String query) {
        Bundle params = new Bundle();
        params.putString(QUERY_PARAM, query);
        mLoaderManager.restartLoader(LOADER_ID, params, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    // ---- LoaderManager.LoaderCallbacks
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        String s = args != null ? args.getString(QUERY_PARAM) : null;
        return new MoviesSearchTask(getContext(), s, mMoviesList);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null && data.size() > 0){
            mMoviesList.clear();
            mMoviesList.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}
