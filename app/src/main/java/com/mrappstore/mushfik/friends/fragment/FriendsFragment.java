package com.mrappstore.mushfik.friends.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.adapter.FriendAdapter;
import com.mrappstore.mushfik.friends.adapter.FriendRequestAdapter;
import com.mrappstore.mushfik.friends.api.ApiClient;
import com.mrappstore.mushfik.friends.api.services.UserInterface;
import com.mrappstore.mushfik.friends.model.FriendsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {

    Context context;

    @BindView(R.id.friend_reqst_rcy)
    RecyclerView friendReqstRcy;
    @BindView(R.id.friends_rcy)
    RecyclerView friendsRcy;

    Unbinder unbinder;

    FriendAdapter friendAdapter;
    FriendRequestAdapter friendRequestAdapter;

    List <FriendsModel.Friend> friends = new ArrayList <>();
    List <FriendsModel.Request> requests = new ArrayList <>();
    @BindView(R.id.request_title)
    TextView requestTitle;
    @BindView(R.id.friend_title)
    TextView friendTitle;
    @BindView(R.id.defaultTextView)
    TextView defaultTextView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        unbinder = ButterKnife.bind(this, view);

        friendAdapter = new FriendAdapter(friends, context);
        friendRequestAdapter = new FriendRequestAdapter(requests, context);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);

        friendReqstRcy.setLayoutManager(linearLayoutManager1);
        friendsRcy.setLayoutManager(linearLayoutManager2);

        friendsRcy.setAdapter(friendAdapter);
        friendReqstRcy.setAdapter(friendRequestAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getListData();
    }

    private void getListData() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map <String, String> params = new HashMap <String, String>();
        params.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call <FriendsModel> call = userInterface.loadFriendsData(params);
        call.enqueue(new Callback <FriendsModel>() {
            @Override
            public void onResponse(Call <FriendsModel> call, Response <FriendsModel> response) {
                if (response != null) {
                    if (response.body().getFriends().size() > 0) {
                        friends.clear();
                        friends.addAll(response.body().getFriends());
                        friendAdapter.notifyDataSetChanged();
                        friendTitle.setVisibility(View.VISIBLE);
                    } else {
                        friendTitle.setVisibility(View.GONE);
                    }

                    if (response.body().getRequests().size() > 0) {
                        requests.clear();
                        requests.addAll(response.body().getRequests());
                        friendRequestAdapter.notifyDataSetChanged();
                        requestTitle.setVisibility(View.VISIBLE);
                    } else {
                        requestTitle.setVisibility(View.GONE);
                    }

                    if (response.body().getRequests().size() == 0 && response.body().getFriends().size() == 0) {
                        defaultTextView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call <FriendsModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();

        requests.clear();
        friendRequestAdapter.notifyDataSetChanged();

        friends.clear();
        friendAdapter.notifyDataSetChanged();
    }
}
