package com.example.eduardorodriguez.comeaqui.profile.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PaymentMethodFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static ArrayList<PaymentObject> data;
    private static MyPaymentMethodRecyclerViewAdapter fa;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaymentMethodFragment() {
    }

    public static void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new PaymentObject(jo));
            }
            fa.updateData(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToList(String jsonString){
        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(jsonString).getAsJsonObject();
        data.add(0, new PaymentObject(jo));
        fa.updateData(data);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PaymentMethodFragment newInstance(int columnCount) {
        PaymentMethodFragment fragment = new PaymentMethodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paymentmethod_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            GetAsyncTask getCards = new GetAsyncTask("GET", getResources().getString(R.string.server) + "my_profile_card/");

            try {
                String response = getCards.execute().get();
                if (response != null)
                    makeList(new JsonParser().parse(response).getAsJsonArray());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            fa = new MyPaymentMethodRecyclerViewAdapter(data, mListener);
            recyclerView.setAdapter(fa);
        }
        return view;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}

class PaymentObject{
    String card_number;
    String expiration_date;
    String card_type;
    String cvv;
    String zip_code;
    String country;
    public PaymentObject(JsonObject jo){
        card_number = jo.get("card_number").getAsString();
        expiration_date = jo.get("expiration_date").getAsString();
        card_type = jo.get("card_type").getAsString();
        cvv = jo.get("cvv").getAsString();
        zip_code = jo.get("zip_code").getAsString();
        country = jo.get("country").getAsString();
    }
}
