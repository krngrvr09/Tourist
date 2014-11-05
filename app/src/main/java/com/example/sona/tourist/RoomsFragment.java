package com.example.sona.tourist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * {@link RoomsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class RoomsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView list;
    JSONObject roomData;
    JSONArray namesArray;
    ArrayList<SingleRoom> rooms = new ArrayList<SingleRoom>();

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
     * @return A new instance of fragment RoomsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomsFragment newInstance(String param1, String param2) {
        RoomsFragment fragment = new RoomsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public RoomsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        list = (ListView) view.findViewById(R.id.roomsList);
        list.setAdapter(new RoomAdapter());
        return view;
    }
    class SingleRoom{
        String name;
        public SingleRoom(String name){
            this.name = name;
        }
    }
    class RoomAdapter extends BaseAdapter {

        public RoomAdapter(){
            new GetDataInAsyncTask(){
            @Override
            protected void onPostExecute(String v){
                    output = v;
                Log.d("roomslist", output);
                try {
                    roomData = new JSONObject(output);
                    namesArray = roomData.getJSONArray("roomnames");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("roomnames", String.valueOf(namesArray));
                for(int i=0;i<namesArray.length();i++){
                    try {
                        rooms.add(new SingleRoom(String.valueOf(namesArray.get(i))));
                        Log.d("roomsize1", String.valueOf(rooms.size()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                RoomAdapter.this.notifyDataSetChanged();

            }
            }.execute(ActivityMain.ServerURL+"/api/rooms/show","");
        }

        @Override
        public int getCount() {
            Log.d("roomsize", String.valueOf(rooms.size()));
            return rooms.size();
        }

        @Override
        public Object getItem(int i) {
            return rooms.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        class ViewHolder {
            TextView name;

            ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.roomName);
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = view;
            ViewHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_room, viewGroup, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            SingleRoom temp = rooms.get(i);
            holder.name.setText(temp.name);

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
