package com.asprime.techmatesupport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import com.asprime.techmatesupport.adapter.MenuAdapter;
import com.asprime.techmatesupport.databinding.ActivityCommonBinding;
import com.asprime.techmatesupport.databinding.NavigationHeaderLayoutBinding;
import com.asprime.techmatesupport.dialogs.ChangePasswordDialog;
import com.asprime.techmatesupport.dialogs.NotificationSetUpDialog;
import com.asprime.techmatesupport.fragment.ClientUserFragment;
import com.asprime.techmatesupport.fragment.CompaniesFragment;
import com.asprime.techmatesupport.fragment.ContentUploadFragment;
import com.asprime.techmatesupport.fragment.DashboardFragment;
import com.asprime.techmatesupport.fragment.DevicesFragment;
import com.asprime.techmatesupport.fragment.MobileAppDeviceFragment;
import com.asprime.techmatesupport.fragment.PendingTicketFragment;
import com.asprime.techmatesupport.fragment.PosKeyFragment;
import com.asprime.techmatesupport.fragment.RequestTicketFragment;
import com.asprime.techmatesupport.fragment.StoreFragment;
import com.asprime.techmatesupport.fragment.SuperUserFragment;
import com.asprime.techmatesupport.fragment.SupportUserFragment;
import com.asprime.techmatesupport.fragment.TicketHistoryFragment;
import com.asprime.techmatesupport.listners.DashboardClickEventListener;
import com.asprime.techmatesupport.model.MenuModel;
import com.asprime.techmatesupport.model.ResponseHandlerModel;
import com.asprime.techmatesupport.network.ClientApi;
import com.asprime.techmatesupport.network.RetrofitClient;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DashboardClickEventListener {
    private ActivityCommonBinding activityCommonBinding;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    String selectedGroup = "";
    PreferenceHandler preferenceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCommonBinding = ActivityCommonBinding.inflate(getLayoutInflater());
        View view = activityCommonBinding.getRoot();
        setContentView(view);

        preferenceHandler = new PreferenceHandler(this);

        /* To hide status bar */
        CommonUtils.changeStatusBar(this);

        /* Set drawer layout */
        setDrawerLayout();

        hitApiToCheckRights("home");

        View viewHeader = activityCommonBinding.navigationView.getHeaderView(0);
        NavigationHeaderLayoutBinding navigationHeaderLayoutBinding = NavigationHeaderLayoutBinding.bind(viewHeader);
        navigationHeaderLayoutBinding.setUserName(preferenceHandler.getUser_name());
        try {
            JSONObject obj = new JSONObject(preferenceHandler.getCompany_information());
            String companyCode = obj.getString("CustCode");
            String companyName = obj.getString("CustName");
            navigationHeaderLayoutBinding.setCompanyName(companyCode + "-" + companyName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navigationHeaderLayoutBinding.setCompanyName("ASPIRE SOFTWARE LTD");
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setDrawerLayout() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, activityCommonBinding.drawerLayout, R.string.nav_open, R.string.nav_close);
        activityCommonBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activityCommonBinding.navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* override the onOptionsItemSelected() function to implement the item click listener callback to open and close the navigation drawer when the icon is clicked */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.confirmation_dialog_layout);
            Window window = dialog.getWindow();
            dialog.setCancelable(false);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);

            dialog.findViewById(R.id.closeDialogBtn).setOnClickListener(v -> dialog.dismiss());

            dialog.findViewById(R.id.confirmPositiveBtn).setOnClickListener(v -> CommonUtils.logOut(this));

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    //    /**
//     * Call API to check user rights
//     * @param flag value to set default fragment
//     */
    public void hitApiToCheckRights(String flag) {
        CommonUtils.progressBarState(true, this);
        ClientApi apiInterface = RetrofitClient.getApiClient().create(ClientApi.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("UserName", preferenceHandler.getUser_name().trim());
        apiInterface.checkRights(hashMap).enqueue(new Callback<ResponseHandlerModel>() {
            @Override
            public void onResponse(@Nullable Call<ResponseHandlerModel> call, @Nullable Response<ResponseHandlerModel> response) {
                if (response != null) {
                    assert response.body() != null;
                    String jsonString = response.body().getD();
                    if (!jsonString.equalsIgnoreCase(preferenceHandler.getUser_rights())) {
                        preferenceHandler.setUser_rights(jsonString);
                        /* Set Default Fragment*/
                        setFragment(new DashboardFragment());
                    }
                }

                try {
                    getRightsForMenu();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (flag.equals("home")) {
                    /* Set Default Fragment */
                    setFragment(new DashboardFragment());
                }
                CommonUtils.progressBarState(false, CommonActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseHandlerModel> call, @NonNull Throwable t) {
                /* Set Default Fragment*/
                setFragment(new DashboardFragment());
                try {
                    getRightsForMenu();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CommonUtils.progressBarState(false, CommonActivity.this);
            }
        });
    }

    private void getRightsForMenu() throws JSONException {
        String userRights = preferenceHandler.getUser_rights();
        prepareMenuData(CommonUtils.getRightListFromString(userRights));
        populateExpandableList(CommonUtils.getRightListFromString(userRights));
    }

    private void prepareMenuData(List<String> rightsList) {
        headerList = new ArrayList<>();
        childList = new HashMap<>();
        MenuModel menuModel = new MenuModel("Home", true, false, getDrawableFromIcon(R.drawable.ic_baseline_home_24));
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Issue Tracker", true, true, getDrawableFromIcon(R.drawable.ic_baseline_issue_tracker_24));
        headerList.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Request Ticket", false, false, getDrawableFromIcon(R.drawable.ic_outline_request_ticket_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Pending Ticket", false, false, getDrawableFromIcon(R.drawable.ic_baseline_pending_ticket_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Ticket History", false, false, getDrawableFromIcon(R.drawable.ic_baseline_ticket_history_24));
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }

        if (rightsList.contains("CompanyRights")) {
            menuModel = new MenuModel("Company Setup", true, true, getDrawableFromIcon(R.drawable.ic_baseline_settings_24));
            headerList.add(menuModel);

            childModelsList = new ArrayList<>();
            childModel = new MenuModel("Companies", false, false, getDrawableFromIcon(R.drawable.ic_baseline_companies_24));
            childModelsList.add(childModel);

            childModel = new MenuModel("Store", false, false, getDrawableFromIcon(R.drawable.ic_baseline_store_24));
            childModelsList.add(childModel);


            if (menuModel.hasChildren) {
                childList.put(menuModel, childModelsList);
            }
        }

        if (rightsList.contains("DeviceRights")) {
            menuModel = new MenuModel("Devices", true, false, getDrawableFromIcon(R.drawable.ic_baseline_desktop_windows_24));
            headerList.add(menuModel);
            if (!menuModel.hasChildren) {
                childList.put(menuModel, null);
            }
        }

        if (rightsList.contains("UsersRights")) {
            menuModel = new MenuModel("Users", true, true, getDrawableFromIcon(R.drawable.ic_baseline_person_add_24));
            headerList.add(menuModel);

            childModelsList = new ArrayList<>();
            childModel = new MenuModel("Client Users", false, false, getDrawableFromIcon(R.drawable.ic_baseline_client_24));
            childModelsList.add(childModel);

            childModel = new MenuModel("Support Users", false, false, getDrawableFromIcon(R.drawable.ic_baseline_support_24));
            childModelsList.add(childModel);

            if(rightsList.contains("SuperUsers")) {
                childModel = new MenuModel("Super Users", false, false, getDrawableFromIcon(R.drawable.profile));
                childModelsList.add(childModel);
            }

            if (menuModel.hasChildren) {
                childList.put(menuModel, childModelsList);
            }
        }

        menuModel = new MenuModel("What's New", true, true, getDrawableFromIcon(R.drawable.ic_baseline_whats_new_manuals_24)); //Menu of Java Tutorials
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();

        childModel = new MenuModel("Windows Desktop", false, false, getDrawableFromIcon(R.drawable.ic_baseline_desktop_windows_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Android/IOS", false, false, getDrawableFromIcon(R.drawable.ic_baseline_phone_android_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Web", false, false, getDrawableFromIcon(R.drawable.ic_baseline_web_24));
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }

        menuModel = new MenuModel("User Manuals", true, true, getDrawableFromIcon(R.drawable.ic_baseline_user_manuals_24));
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();
        childModel = new MenuModel("Windows Desktop", false, false, getDrawableFromIcon(R.drawable.ic_baseline_desktop_windows_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Android/IOS", false, false, getDrawableFromIcon(R.drawable.ic_baseline_phone_android_24));
        childModelsList.add(childModel);

        childModel = new MenuModel("Web", false, false, getDrawableFromIcon(R.drawable.ic_baseline_web_24));
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }

        if(rightsList.contains("InstallationGuide")) {
            menuModel = new MenuModel("Installation Manual", true, true, getDrawableFromIcon(R.drawable.ic_baseline_install_desktop_24));
            headerList.add(menuModel);
            childModelsList = new ArrayList<>();
            childModel = new MenuModel("Manual", false, false, getDrawableFromIcon(R.drawable.ic_baseline_whats_new_manuals_24));
            childModelsList.add(childModel);

            if (menuModel.hasChildren) {
                childList.put(menuModel, childModelsList);
            }
        }

        if(rightsList.contains("APKActivationView")) {
            menuModel = new MenuModel("Mobile App Activation", true, true, getDrawableFromIcon(R.drawable.ic_baseline_install_mobile_24));
            headerList.add(menuModel);

            childModelsList = new ArrayList<>();
            if(rightsList.contains("APKActivation")) {
                childModel = new MenuModel("Activation", false, false, getDrawableFromIcon(R.drawable.ic_baseline_done_all_24));
                childModelsList.add(childModel);
            }

            childModel = new MenuModel("View Devices", false, false, getDrawableFromIcon(R.drawable.ic_baseline_phone_android_24));
            childModelsList.add(childModel);

            if (menuModel.hasChildren) {
                childList.put(menuModel, childModelsList);
            }
        }

        if(rightsList.contains("APPActivateView")) {
            menuModel = new MenuModel("Mobile App Activation New", true, true, getDrawableFromIcon(R.drawable.ic_baseline_mobile_friendly_24));
            headerList.add(menuModel);

            childModelsList = new ArrayList<>();
            if(rightsList.contains("APPLicenses")) {
                childModel = new MenuModel("Issue Licenses", false, false, getDrawableFromIcon(R.drawable.ic_baseline_license_24));
                childModelsList.add(childModel);
            }

            if(rightsList.contains("APPActivateRequest")) {
                childModel = new MenuModel("Activation Request", false, false, getDrawableFromIcon(R.drawable.ic_baseline_attach_file_24));
                childModelsList.add(childModel);
            }

            if(rightsList.contains("APPActivated")) {
                childModel = new MenuModel("Activated Device", false, false, getDrawableFromIcon(R.drawable.ic_baseline_done_all_24));
                childModelsList.add(childModel);
            }

            childModel = new MenuModel("View Activated Device", false, false, getDrawableFromIcon(R.drawable.ic_baseline_phone_android_24));
            childModelsList.add(childModel);

            if (rightsList.contains("APPAudit")) {
                childModel = new MenuModel("View Audit", false, false, getDrawableFromIcon(R.drawable.ic_baseline_view_audit_24));
                childModelsList.add(childModel);
            }

            if (rightsList.contains("APKVersion")) {
                childModel = new MenuModel("App Version Control", false, false, getDrawableFromIcon(R.drawable.ic_baseline_app_version_control24));
                childModelsList.add(childModel);
            }

            if (rightsList.contains("APPClientURL")) {
                childModel = new MenuModel("App Clients URLs", false, false, getDrawableFromIcon(R.drawable.ic_baseline_http_24));
                childModelsList.add(childModel);
            }

            if (rightsList.contains("APPBlackList")) {
                childModel = new MenuModel("App Black Listed", false, false, getDrawableFromIcon(R.drawable.ic_baseline_block_24));
                childModelsList.add(childModel);
            }

            if (menuModel.hasChildren) {
                childList.put(menuModel, childModelsList);
            }
        }

        if(rightsList.contains("POSitiveLC")) {
            menuModel = new MenuModel("Positive License Key", true, false, getDrawableFromIcon(R.drawable.ic_baseline_vpn_key_24));
            headerList.add(menuModel);
            if (!menuModel.hasChildren) {
                childList.put(menuModel, null);
            }
        }

        menuModel = new MenuModel("Change Password", true, false, getDrawableFromIcon(R.drawable.ic_baseline_settings_24));
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Notification Setup", true, false, getDrawableFromIcon(R.drawable.ic_baseline_notifications_24));
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
    }

    private Drawable getDrawableFromIcon(int res) {
        return ResourcesCompat.getDrawable(getResources(), res, null);
    }

    private void populateExpandableList(List<String> rightListFromString) {
        ExpandableListView expandableListView = activityCommonBinding.expendableMenu;
        MenuAdapter menuAdapter = new MenuAdapter(this, headerList, childList);
        expandableListView.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (headerList.get(groupPosition).isGroup) {
                selectedGroup = headerList.get(groupPosition).getMenuName();
                if (!headerList.get(groupPosition).hasChildren) {
                    hitApiToCheckRights(headerList.get(groupPosition).getMenuName());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(headerList.get(groupPosition).getMenuName());
                    if (headerList.get(groupPosition).getMenuName().equalsIgnoreCase("home")) {
                        setFragment(new DashboardFragment());
                        Objects.requireNonNull(getSupportActionBar()).setTitle("TechMate Client");
                    } else if (headerList.get(groupPosition).getMenuName().equalsIgnoreCase(getResources().getString(R.string.positive_license_key_item))) {
                        setFragment(new PosKeyFragment());
                    } else if (headerList.get(groupPosition).getMenuName().equalsIgnoreCase(getResources().getString(R.string.device))) {
                        setFragment(new DevicesFragment());
                    } else if (headerList.get(groupPosition).getMenuName().equalsIgnoreCase(getResources().getString(R.string.change_password_item))) {
                        Objects.requireNonNull(getSupportActionBar()).setTitle("TechMate Support");
                        ChangePasswordDialog dialogFragment = new ChangePasswordDialog();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("updatePassword");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        dialogFragment.show(ft, "updatePassword");
                    } else if (headerList.get(groupPosition).getMenuName().equalsIgnoreCase("Notification Setup")) {
                        Objects.requireNonNull(getSupportActionBar()).setTitle("TechMate Support");
                        NotificationSetUpDialog dialogFragment = new NotificationSetUpDialog();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("notificationSetUp");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        dialogFragment.show(ft, "notificationSetUp");
                    }
                    onBackPressed();
                }
            }
            return false;
        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (childList.get(headerList.get(groupPosition)) != null) {
                hitApiToCheckRights("");
                MenuModel model = Objects.requireNonNull(childList.get(headerList.get(groupPosition))).get(childPosition);
                Objects.requireNonNull(getSupportActionBar()).setTitle(model.getMenuName());
                if (model.getMenuName().equalsIgnoreCase(getResources().getString(R.string.request_ticket_item))) {
                    setFragment(new RequestTicketFragment());
                } else if (model.getMenuName().equalsIgnoreCase(getResources().getString(R.string.pending_ticket_item))) {
                    setFragment(new PendingTicketFragment());
                } else if (model.getMenuName().equalsIgnoreCase(getResources().getString(R.string.ticket_history_item))) {
                    setFragment(new TicketHistoryFragment());
                } else if (model.getMenuName().equalsIgnoreCase("Companies")) {
                    setFragment(new CompaniesFragment());
                }  else if (model.getMenuName().equalsIgnoreCase("Store")) {
                    setFragment(new StoreFragment());
                } else if (model.getMenuName().equalsIgnoreCase("Client Users")) {
                    setFragment(new ClientUserFragment());
                } else if (model.getMenuName().equalsIgnoreCase("Support Users")) {
                    setFragment(new SupportUserFragment());
                } else if (model.getMenuName().equalsIgnoreCase("Super Users")) {
                    setFragment(new SuperUserFragment());
                } else if (model.getMenuName().equalsIgnoreCase("Content Upload") && selectedGroup.equalsIgnoreCase(getResources().getString(R.string.whats_new_manuals_item))) {
                    ContentUploadFragment contentUploadFragment = new ContentUploadFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragmentFlag", "WCU");
                    contentUploadFragment.setArguments(bundle);
                    setFragment(contentUploadFragment);
                } else if (model.getMenuName().equalsIgnoreCase("Content Upload") && selectedGroup.equalsIgnoreCase(getResources().getString(R.string.user_manuals_item))) {
                    ContentUploadFragment contentUploadFragment = new ContentUploadFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragmentFlag", "UCU");
                    contentUploadFragment.setArguments(bundle);
                    setFragment(contentUploadFragment);
                } else if (model.getMenuName().equalsIgnoreCase("Activation")) {
                    MobileAppDeviceFragment mobileAppDeviceFragment = new MobileAppDeviceFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "activation");
                    mobileAppDeviceFragment.setArguments(bundle);
                    setFragment(mobileAppDeviceFragment);
                } else if (model.getMenuName().equalsIgnoreCase("View Devices")) {
                    MobileAppDeviceFragment mobileAppDeviceFragment = new MobileAppDeviceFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "viewDevices");
                    mobileAppDeviceFragment.setArguments(bundle);
                    setFragment(mobileAppDeviceFragment);
                }
                onBackPressed();
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = activityCommonBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("TechMate Client");
            String f = getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName();
            if (f.equalsIgnoreCase("DashboardFragment")) {
                super.onBackPressed();
            } else {
//                setFragment(new DashboardFragment());
            }
        }
    }

    @Override
    public void onMenuItemClick(@NonNull String flag) {
        if (flag.equalsIgnoreCase("pendingTicket")) {
            setFragment(new PendingTicketFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Pending Ticket");
        }
    }
}