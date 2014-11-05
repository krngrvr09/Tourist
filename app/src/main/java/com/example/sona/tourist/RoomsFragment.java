package com.example.sona.tourist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
    JSONArray roomIds;
    ArrayList<SingleRoom> rooms = new ArrayList<SingleRoom>();
    int roomCheckedIn;
    boolean checkedIn;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_room, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // below "case" is not caausing any problems, but why should it be R.id.new_discussion
            case R.id.add_room:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                View inf = inflater.inflate(R.layout.add_room_dialog, null);
                final EditText et1 = (EditText) inf.findViewById(R.id.AnnouncementTitle);
                final EditText et2 = (EditText) inf.findViewById(R.id.AnnouncementBody);
                builder.setView(inf);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String title = et1.getText().toString();
                        String body = et2.getText().toString();

                        String postData = "room[accessPoint]="+title+";room[name]="+body;

                        new GetDataInAsyncTask(){
                            @Override
                            protected void onPostExecute(String v) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                RoomsFragment.this.refresh();
                            }
                        }.execute(ActivityMain.ServerURL+"/api/rooms/create/"+ActivityMain.userId, postData);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                String s="Example: Office";
                SpannableString ss=  new SpannableString(s);
                ss.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 15, 0);
                builder.setMessage(ss);
                String l="Create Room";
                SpannableString ll=  new SpannableString(l);
                ll.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 11, 0);
                builder.setTitle(ll);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh(){
        RoomAdapter r = new RoomAdapter();
        list.setAdapter(r);
        ((RoomAdapter)list.getAdapter()).notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        setHasOptionsMenu(true);
        list = (ListView) view.findViewById(R.id.roomsList);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //ActivityUser.from_where = "hahahha";
                //Log.d("herehere",ActivityUser.from_where);
                int roomtoopen = 0;
                try {
                    roomtoopen = Integer.parseInt(String.valueOf(roomIds.get(position)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                    Intent intent = new Intent(getActivity(), ActivityUser.class);
                    intent.putExtra("roomNumber",roomtoopen);
                    try {
                        intent.putExtra("roomName", String.valueOf(namesArray.get(position)).replace("[", "").replace("]", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);
            }
        });

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
                    roomIds = roomData.getJSONArray("roomIds");
                    roomCheckedIn = roomData.getInt("checkin");
                    if(roomCheckedIn!=0){
                        checkedIn =true;
                    }
                    else{
                        checkedIn = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("roomnames", String.valueOf(namesArray));
                rooms.clear();
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
            }.execute(ActivityMain.ServerURL+"/api/rooms/show/"+ActivityMain.userId,"");
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
            TextView checkInButton;

            ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.roomName);
                checkInButton = (TextView) v.findViewById(R.id.checkInButton);
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
            if(checkedIn) {
                try {
                    Log.d("roomCheckedIn", String.valueOf(roomCheckedIn));
                    Log.d("roomCheckedIn1", String.valueOf(Integer.parseInt(String.valueOf(roomIds.get(i)))));
                    if (roomCheckedIn == Integer.parseInt(String.valueOf(roomIds.get(i)))) {
                        holder.checkInButton.setVisibility(View.VISIBLE);
                        holder.checkInButton.setText("Check Out");
                    }
                    else{
                        holder.checkInButton.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                holder.checkInButton.setVisibility(View.VISIBLE);
            }
            final int finalposition = i;
            final ViewHolder finalHolder = holder;
            holder.checkInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalHolder.checkInButton.getText().equals("Check In")) {
                        try {
                            new GetDataInAsyncTask() {
                                @Override
                                protected void onPostExecute(String v) {
                                    finalHolder.checkInButton.setText("Check Out");
                                    RoomsFragment.this.refresh();

                                }
                            }.execute(ActivityMain.ServerURL + "/api/checkin/" + ActivityMain.userId + "/" + String.valueOf(roomIds.get(finalposition)), "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                            new GetDataInAsyncTask() {
                                @Override
                                protected void onPostExecute(String v) {
                                    finalHolder.checkInButton.setText("Check In");
                                    RoomsFragment.this.refresh();
                                }
                            }.execute(ActivityMain.ServerURL + "/api/checkout/" + ActivityMain.userId, "");

                    }
                }
            });

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
