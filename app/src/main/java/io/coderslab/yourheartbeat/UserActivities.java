package io.coderslab.yourheartbeat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

import CustomComponents.CustomUserActivityItem;
import Utilities.User;
import Utilities.utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserActivities.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserActivities#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserActivities extends Fragment implements User.FetchUserActivities {
    private static final String className = "UserActivities.java";

    private User currentUser = new User();

    private View myView;

    LinearLayout userActivitiesContainer;

    private OnFragmentInteractionListener mListener;

    public UserActivities() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserActivities.
     */
    public static UserActivities newInstance(String param1, String param2) {
        UserActivities fragment = new UserActivities();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser.setFetchUserActivitiesListener(this);
        currentUser.fetchAllRemoteActivitiesUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activities, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        myView = v;
        // reference elements
        userActivitiesContainer = myView.findViewById(R.id.fragmentUserActivityContainer);

    }

    private void addElementToUserActivityScrollview() {

        Boolean noException = false;
        int numberOfActivities = 0;
        try {
            numberOfActivities = currentUser.getUserActivities().size();
            noException = true;
        } catch (Exception e) {
            utils.logError(e.getMessage(), className);
        }

        // Loop through each activities
        if (noException) {
            if (numberOfActivities > 0) {
                try {
                    Iterator it = currentUser.getUserActivities().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        Object temp = pair.getValue();
                        if (temp instanceof Map) {
                            try {
                                final Map thisActivity = (Map) temp;
                                CustomUserActivityItem myItem = new CustomUserActivityItem(getContext());
                                myItem.setTitle(thisActivity.get("Title").toString());
                                myItem.setDescription(thisActivity.get("Description").toString());
                                myItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getContext(), DisplayUserActivity.class);
                                        intent.putExtra("title", thisActivity.get("Title").toString());
                                        intent.putExtra("description", thisActivity.get("Description").toString());
                                        startActivity(intent);
                                        (getActivity()).overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                                    }
                                });
                                userActivitiesContainer.addView(myItem);
                            } catch (Exception e) {
                                utils.logError(e.getMessage(), className);
                            }
                        } else {
                            utils.logError("Unknown error occured!", className);
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }

                } catch (Exception e) {
                    showNoActivitiesView();
                    utils.logError(e.getMessage(), className);
                }
            } else {
                showNoActivitiesView();
            }
        } else {
            showNoActivitiesView();
        }
    }

    private void showNoActivitiesView() {
        TextView noActivitiesTextView = new TextView(getContext());
        noActivitiesTextView.setText(R.string.no_recent_activities);
        noActivitiesTextView.setTextColor(getResources().getColor(R.color.colorSchemeGray));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                ((int) LinearLayout.LayoutParams.MATCH_PARENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);
        noActivitiesTextView.setLayoutParams(params);
        noActivitiesTextView.setGravity(Gravity.CENTER);
        userActivitiesContainer.addView(noActivitiesTextView);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void remoteFetchUserActivitiesSuccess() {
        addElementToUserActivityScrollview();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
