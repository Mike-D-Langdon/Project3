package com.example.shoutout.activity.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shoutout.R;
import com.example.shoutout.db.UsersRepository;
import com.example.shoutout.dbo.User;

/**
 * Fragment that allows the end user to search for other users and go to their profiles.
 *
 * @author Corneilious Eanes
 * @since April 29, 2021
 */
public class SearchFragment extends Fragment {

    /**
     * Logging tag
     */
    private static final String TAG = SearchFragment.class.getSimpleName();

    public SearchFragment() {
    }

    /**
     * Edit text field that allows the specification of the query to send to the database
     * @see UsersRepository#getFromUsername(String)
     */
    private EditText edit_query;
    /**
     * Text view to display if there were no results returned from the database
     */
    private TextView text_noResults;
    /**
     * Linear layout showing all the results
     */
    private LinearLayout layout_results;
    /**
     * Button that allows the query to be submitted to the database
     */
    private Button button_search;
    /**
     * Listener that fires when the end user clicks on a profile from the result list
     */
    private UserSearchResultFragment.OnProfileClickListener listener;

    /**
     * Creates a new instance of this fragment.
     * @return A new instance of this fragment
     * @see SearchFragment
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        // don't need any arguments
        return fragment;
    }

    /**
     * Update the listener that is fired when the end user clicks on a profile from the result list
     * @param listener A listener is that fired when the end user clicks on a profile from the
     *                 result list
     */
    public void setOnProfileClickListener(UserSearchResultFragment.OnProfileClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            edit_query = getView().findViewById(R.id.edit_search_query);
            text_noResults = getView().findViewById(R.id.text_search_noResults);
            layout_results = getView().findViewById(R.id.layout_search_results);
            button_search = getView().findViewById(R.id.button_search);

            button_search.setOnClickListener(v -> {
                final String query = edit_query.getText().toString();
                // don't submit the query if it's empty
                if (!query.isEmpty()) {
                    // disable the search button while the request is being processed by the server
                    button_search.setEnabled(false);
                    UsersRepository.getInstance().getFromUsername(query).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = task.getResult();
                            // remove everything except the noResults text, the 0th element
                            layout_results.removeViews(1, layout_results.getChildCount() - 1);
                            if (user == null) {
                                // display the lack of results to the user if the user is null
                                text_noResults.setVisibility(View.VISIBLE);
                            } else {
                                // otherwise, create a new fragment displaying basic user information
                                text_noResults.setVisibility(View.GONE);
                                UserSearchResultFragment frag = UserSearchResultFragment.newInstance(user);
                                // don't forget to set the profile click listener!
                                frag.setOnProfileClickListener(listener);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.layout_search_results, frag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } else {
                            Log.w(TAG, "Could not search for users", task.getException());
                        }
                        // re-enable the button once the request is complete, successful or not
                        button_search.setEnabled(true);
                    });
                }
            });
        }
    }

}