package com.moneydesktop.finance.tablet.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moneydesktop.finance.ApplicationContext;
import com.moneydesktop.finance.BaseFragment;
import com.moneydesktop.finance.R;
import com.moneydesktop.finance.data.BankLogoManager;
import com.moneydesktop.finance.database.AccountType;
import com.moneydesktop.finance.database.Bank;
import com.moneydesktop.finance.tablet.adapter.AccountTypesAdapter;
import com.moneydesktop.finance.util.UiUtils;
import com.moneydesktop.finance.views.PopupWindowAtLocation;
import com.moneydesktop.finance.views.SlidingDrawerRightSide;

import java.util.ArrayList;
import java.util.List;

public class AccountTypesTabletFragment extends BaseFragment {
    private ExpandableListView mExpandableListView;
    private static SlidingDrawerRightSide sRightDrawer;
    private View mFooter;
	
	public static AccountTypesTabletFragment newInstance(int position) {	
		AccountTypesTabletFragment frag = new AccountTypesTabletFragment();
		frag.setPosition(position);
		
        Bundle args = new Bundle();
        frag.setArguments(args);
        
        return frag;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        this.mActivity.onFragmentAttached(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mRoot = inflater.inflate(R.layout.activity_account_types, null);
		mFooter = inflater.inflate(R.layout.account_type_list_footer, null);
		mExpandableListView = (ExpandableListView) mRoot.findViewById(R.id.accounts_expandable_list_view);
		sRightDrawer = (SlidingDrawerRightSide) mRoot.findViewById(R.id.account_slider);
		setupView();
		
		return mRoot;
	}
	
	private void setupView() {
		final LinearLayout panelLayoutHolder = (LinearLayout)mRoot.findViewById(R.id.panel_layout_holder);
        mExpandableListView.setGroupIndicator(null);
        
        List<AccountType> accountTypes = ApplicationContext.getDaoSession().getAccountTypeDao().loadAll();
        List<AccountType> accountTypesFiltered = new ArrayList<AccountType>();
        
        for (AccountType type : accountTypes) {  //This could possibly be optimized by throwing a "where" in the query builder
        	if (!type.getBankAccounts().isEmpty()) {
        		accountTypesFiltered.add(type);
        	}
        }
        
        if (!accountTypesFiltered.isEmpty()) {        	
        	mExpandableListView.addFooterView(mFooter);
        	mExpandableListView.setAdapter(new AccountTypesAdapter(accountTypesFiltered, mActivity, mExpandableListView));
        } else {
        	Toast.makeText(mActivity, "No Accounts types that have bank accounts...show empty state", Toast.LENGTH_SHORT).show();
        }

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
	            ((AccountTypesAdapter)mExpandableListView.getExpandableListAdapter()).notifyDataSetChanged();
	            return false;
			}
		});
        
        panelLayoutHolder.setOnTouchListener(new View.OnTouchListener() {			
			public boolean onTouch(View view, MotionEvent event) {
				return true;
			}
		});

        
        final ViewGroup.LayoutParams layoutParams = panelLayoutHolder.getLayoutParams();
        layoutParams.width = UiUtils.getMinimumPanalWidth(mActivity);
        panelLayoutHolder.setLayoutParams(layoutParams);

        setupDrawer(layoutParams, mActivity);
        initializeDrawer(panelLayoutHolder);
	}
	
	
	/**
	 * Setup the Panel/Drawer to show all banks attached.
	 * @param panelLayoutHolder -- the panel container
	 */
	private void initializeDrawer (LinearLayout panelLayoutHolder) {
		List<Bank> banksList = ApplicationContext.getDaoSession().getBankDao().loadAll();
    	
    	panelLayoutHolder.addView(getPanelHeader());

        //For every bank that is attached, add it to the Drawer
        for (Bank bank : banksList) {
            //create the view to be attached to Drawer
        	panelLayoutHolder.addView(populateDrawerView(bank, panelLayoutHolder));
        }
    }

	/**
	 * Adds the header Text to the panel
	 * @return headerView
	 */
    private View getPanelHeader() {
    	LayoutInflater layoutInflater = mActivity.getLayoutInflater();
    	final View headerView = layoutInflater.inflate(R.layout.tablet_panel_header, null); 
		return headerView;
	}

    /**
     * Creates a View of a bank represented on the right panel.
     * @param bank -the bank to be added
     * @param panelLayoutHolder 
     * @return bank view 
     */
	private View populateDrawerView (final Bank bank, final LinearLayout panelLayoutHolder) {
        LayoutInflater layoutInflater = mActivity.getLayoutInflater();
        final View bankTypeAccountView = layoutInflater.inflate(R.layout.bank_account, null);
        ImageView bankImage = (ImageView)bankTypeAccountView.findViewById(R.id.bank_account_image);
        
        BankLogoManager.getBankImage(bankImage, bank.getBankId());
        
        TextView bankName = (TextView)bankTypeAccountView.findViewById(R.id.account_bank_name);
        
        bankName.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        bankName.setText(bank.getBankName());
        
        bankTypeAccountView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RelativeLayout parentView = (RelativeLayout)getActivity().findViewById(R.id.account_types_container);
				
				List<OnClickListener> onClickListeners = new ArrayList<View.OnClickListener>();
				
				onClickListeners.add(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "REFRESH DATA", Toast.LENGTH_SHORT).show();
					}
				});
				
				onClickListeners.add(new OnClickListener() { 	
					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "REMOVE", Toast.LENGTH_SHORT).show();
					}
				});
				
				onClickListeners.add(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "UPDATE USERNAME AND PASSWORD", Toast.LENGTH_SHORT).show();
					}
				});
				
				new PopupWindowAtLocation(getActivity(), (ViewGroup) bankTypeAccountView, parentView, sRightDrawer.getLeft() - bankTypeAccountView.getWidth(), (int)bankTypeAccountView.getTop() + 10, getActivity().getResources().getStringArray(R.array.bank_selection_popup), onClickListeners);
			}
		});

        return bankTypeAccountView;
    }


	/**
	 * Drawer's width is set to a percentage of screen.
	 * @param layoutParams
	 * @param activity
	 * @return the drawer
	 */
    public static SlidingDrawerRightSide setupDrawer (final ViewGroup.LayoutParams layoutParams, Activity activity) {
        final ViewGroup.LayoutParams drawerLayoutParams = sRightDrawer.getLayoutParams();

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon);
        int width = bitmap.getWidth();
        bitmap.recycle();

        drawerLayoutParams.width = layoutParams.width + width;
        drawerLayoutParams.height = UiUtils.getScreenHeight(activity) ;
        sRightDrawer.setLayoutParams(drawerLayoutParams);

        return sRightDrawer;
    }
	
	@Override
	public String getFragmentTitle() {
		return null;
	}

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
