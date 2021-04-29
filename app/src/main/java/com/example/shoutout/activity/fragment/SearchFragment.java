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

public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    public SearchFragment() {
    }

    private EditText edit_query;
    private TextView text_noResults;
    private LinearLayout layout_results;
    private Button button_search;
    private UserSearchResultFragment.OnProfileClickListener listener;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

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
                if (!query.isEmpty()) {
                    button_search.setEnabled(false);
                    UsersRepository.getInstance().getFromUsername(query).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = task.getResult();
                            // remove everything except the noResults text, the 0th element
                            layout_results.removeViews(1, layout_results.getChildCount() - 1);
                            if (user == null) {
                                text_noResults.setVisibility(View.VISIBLE);
                            } else {
                                text_noResults.setVisibility(View.GONE);
                                UserSearchResultFragment frag = UserSearchResultFragment.newInstance(user);
                                frag.setOnProfileClickListener(listener);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.layout_search_results, frag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } else {
                            Log.w(TAG, "Could not search for users", task.getException());
                        }
                        button_search.setEnabled(true);
                    });
                }
            });
        }
    }

}