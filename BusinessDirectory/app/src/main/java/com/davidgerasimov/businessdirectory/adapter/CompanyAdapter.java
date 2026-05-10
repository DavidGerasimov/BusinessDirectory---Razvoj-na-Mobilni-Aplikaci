package com.davidgerasimov.businessdirectory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davidgerasimov.businessdirectory.R;
import com.davidgerasimov.businessdirectory.model.Company;

import java.util.List;

public class CompanyAdapter extends ArrayAdapter<Company> {

    private Context context;
    private List<Company> companies;

    public CompanyAdapter(Context context, List<Company> companies) {
        super(context, 0, companies);
        this.context = context;
        this.companies = companies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_company, parent, false);
        }

        Company company = companies.get(position);

        ImageView logo = convertView.findViewById(R.id.companyLogo);
        TextView name = convertView.findViewById(R.id.companyName);
        TextView address = convertView.findViewById(R.id.companyAddress);
        TextView phone = convertView.findViewById(R.id.companyPhone);
        TextView website = convertView.findViewById(R.id.companyWebsite);

        name.setText(company.getName());
        address.setText(company.getAddress());
        phone.setText(company.getPhone());
        website.setText(company.getWebsite());

        if (company.getLogoUrl() != null && !company.getLogoUrl().isEmpty()) {
            Glide.with(context)
                    .load(company.getLogoUrl())
                    .placeholder(com.davidgerasimov.businessdirectory.R.mipmap.ic_launcher)
                    .into(logo);
        } else {
            logo.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }

    public void updateData(List<Company> newCompanies) {
        companies.clear();
        companies.addAll(newCompanies);
        notifyDataSetChanged();
    }
}