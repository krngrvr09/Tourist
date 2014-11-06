package com.example.sona.tourist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class UsersFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView list;
    JSONObject usersData;
    JSONArray usersList;
    JSONArray whereUsers;

    int roomNumber;
    SwipeRefreshLayout swipeLayout;

    ArrayList<SingleUser> userArrayList = new ArrayList<SingleUser>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_users, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.ptr_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(R.color.backpackblue,
                R.color.backpackorange,
                R.color.backpackblue,
                R.color.backpackorange);

        roomNumber = getArguments().getInt("roomNumber");
        list = (ListView) view.findViewById(R.id.usersList);
        list.setAdapter(new UserAdapter());
        return view;
    }

    @Override
    public void onRefresh() {
        UserAdapter u = new UserAdapter();
        list.setAdapter(u);
        ((UserAdapter)list.getAdapter()).notifyDataSetChanged();

    }

    class SingleUser{
        String name;
        int where;
        public SingleUser(String name, int where){
            this.name = name;
            this.where = where;
        }
    }
    class UserAdapter extends BaseAdapter {
        public UserAdapter(){
            new GetDataInAsyncTask(){
                @Override
                protected void onPostExecute(String v){
                    output = v;
                    Log.d("usersoutput", output);
                    try {
                        usersData = new JSONObject(output);
                        usersList = usersData.getJSONArray("userNames");
                        whereUsers = usersData.getJSONArray("whereUsers");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userArrayList.clear();
                    for(int i=0;i<usersList.length();i++){
                        try {
                            userArrayList.add(new SingleUser(String.valueOf(usersList.get(i)), Integer.parseInt(String.valueOf(whereUsers.get(i)))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    UserAdapter.this.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);



                }
            }.execute(ActivityMain.ServerURL+"/api/users/showall/"+roomNumber,"");
        }

        @Override
        public int getCount() {
            return userArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return userArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        class ViewHolder {
            TextView name;
            TextView whetherCheckedIn;

            ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.userName);
                whetherCheckedIn = (TextView) v.findViewById(R.id.whetherCheckedIn);
            }
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = view;
            ViewHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_user, viewGroup, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            SingleUser temp = userArrayList.get(i);
            holder.name.setText(String.valueOf(temp.name));
            if(temp.where==roomNumber) {
                holder.whetherCheckedIn.setText("In");
            }
            else{
                holder.whetherCheckedIn.setText("Out");

            }
            return row;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onFragmentInteraction(Uri uri);
    }

}
