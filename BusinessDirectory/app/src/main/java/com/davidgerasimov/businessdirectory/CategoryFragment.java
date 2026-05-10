package com.davidgerasimov.businessdirectory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.davidgerasimov.businessdirectory.adapter.CompanyAdapter;
import com.davidgerasimov.businessdirectory.api.CompanyRepository;
import com.davidgerasimov.businessdirectory.model.Company;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;
    private CompanyAdapter adapter;
    private List<Company> companyList = new ArrayList<>();
    private CompanyRepository repository = new CompanyRepository();

    public static CategoryFragment newInstance(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ListView listView = view.findViewById(R.id.listView);
        adapter = new CompanyAdapter(getContext(), companyList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            Company company = companyList.get(position);
            Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
            intent.putExtra("name", company.getName());
            intent.putExtra("address", company.getAddress());
            intent.putExtra("phone", company.getPhone());
            intent.putExtra("email", company.getEmail());
            intent.putExtra("website", company.getWebsite());
            intent.putExtra("category", company.getCategory());
            intent.putExtra("logoUrl", company.getLogoUrl());
            intent.putExtra("latitude", company.getLatitude());
            intent.putExtra("longitude", company.getLongitude());
            startActivity(intent);
        });

        loadCompanies(null);
        return view;
    }

    public void loadCompanies(String search) {
        repository.getCompaniesByCategory(category, search, new CompanyRepository.CompanyCallback() {
            @Override
            public void onSuccess(List<Company> companies) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        companyList.clear();
                        companyList.addAll(companies);
                        adapter.updateData(companies);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(),
                                "Error loading companies: " + error,
                                android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}