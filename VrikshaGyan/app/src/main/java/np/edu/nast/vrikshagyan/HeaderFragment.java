package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class HeaderFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_header, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgLogo = view.findViewById(R.id.imgLogo);

        SearchView searchView =  view.findViewById(R.id.searchView);
        String jwtToken = SharedPreferencesUtil.getToken(requireContext());

        // Check if token is available
        if (jwtToken == null) {
            // Token is not available or expired, handle accordingly
            Toast.makeText(requireContext(), "Token not available, redirecting to login", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return; // Exit method if token is null
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sendSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });




    }
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void sendSearchQuery(String query) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("SEARCH_QUERY", query);
        startActivity(intent);
    }


}
