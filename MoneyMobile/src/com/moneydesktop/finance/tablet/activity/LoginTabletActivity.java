package com.moneydesktop.finance.tablet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.moneydesktop.finance.BaseActivity;
import com.moneydesktop.finance.DebugActivity;
import com.moneydesktop.finance.R;
import com.moneydesktop.finance.data.DataBridge;
import com.moneydesktop.finance.data.DemoData;
import com.moneydesktop.finance.model.EventMessage;
import com.moneydesktop.finance.model.User;
import com.moneydesktop.finance.util.Animator;
import com.moneydesktop.finance.util.DialogUtils;
import com.moneydesktop.finance.util.Fonts;
import com.moneydesktop.finance.util.UiUtils;
import com.moneydesktop.finance.views.LabelEditText;

import de.greenrobot.event.EventBus;

public class LoginTabletActivity extends BaseActivity {
	
	private final String TAG = "LoginActivity";
	
	private ViewFlipper viewFlipper;
	private LinearLayout buttonView, credentialView;
	private Button loginViewButton, demoButton, loginButton, cancelButton, submitButton;
	private LabelEditText username, password, signupName, signupEmail, signupBank;
	private ImageView logo;
	private TextView signupText, loginText, messageTitle, messageBody, cancelText, needAccount, nagBank;
	private Animation inLeft, inRight, outLeft, outRight;
	
	private boolean failed = false;
	
	@Override
	public void onBackPressed() {
		if (viewFlipper.indexOfChild(viewFlipper.getCurrentView()) == 1) {
			cancel();
		} else {
			super.onBackPressed();
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tablet_login_view);

        setupAnimations();
        setupView();
        
        addDemoCredentials();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		toDashboard();
	}
	
	private void toDashboard() {

		DialogUtils.hideProgress();
		
		if (User.getCurrentUser() != null) {
			
	    	Intent i = new Intent(this, DashboardTabletActivity.class);
	    	startActivity(i);
		}
	}
	
	private void addDemoCredentials() {
		
		username.setText("saul.howard@moneydesktop.com");
		password.setText("password123");
	}
	
	@SuppressLint("NewApi")
	private void setupView() {
		
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		buttonView = (LinearLayout) findViewById(R.id.button_view);
		credentialView = (LinearLayout) findViewById(R.id.credentials);
		configureFlipper();
		
		username = (LabelEditText) findViewById(R.id.username_field);
		password = (LabelEditText) findViewById(R.id.password_field);
		
		loginViewButton = (Button) findViewById(R.id.login_view_button);
		demoButton = (Button) findViewById(R.id.demo_button);
		
		loginButton = (Button) findViewById(R.id.login_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		
		logo = (ImageView) findViewById(R.id.logo);
		
		signupText = (TextView) findViewById(R.id.signup);
		loginText = (TextView) findViewById(R.id.login_text);
	    signupName = (LabelEditText) findViewById(R.id.signup_name);
		signupEmail = (LabelEditText) findViewById(R.id.signup_email);
	    signupBank = (LabelEditText) findViewById(R.id.signup_bank);
		messageTitle = (TextView) findViewById(R.id.message_title);
		messageBody = (TextView) findViewById(R.id.message_body);
		cancelText = (TextView) findViewById(R.id.cancel_text);
		submitButton = (Button) findViewById(R.id.submit_button);
		needAccount = (TextView) findViewById(R.id.need_account);
		nagBank = (TextView) findViewById(R.id.nag_bank);
		Fonts.applyPrimaryFont(loginViewButton, 20);
		Fonts.applyPrimaryFont(demoButton, 20);
		Fonts.applyPrimaryFont(loginButton, 20);
		Fonts.applyPrimaryFont(cancelButton, 20);
		Fonts.applyPrimaryFont(submitButton, 20);
		Fonts.applyPrimaryFont(signupText, 15);
		Fonts.applyPrimaryFont(loginText, 15);
		Fonts.applyPrimaryFont(cancelText, 15);
		Fonts.applyPrimarySemiBoldFont(username, 25);
		Fonts.applyPrimarySemiBoldFont(password, 25);
		Fonts.applyPrimaryBoldFont(needAccount, 25);
		Fonts.applySecondaryItalicFont(nagBank, 15);
		addListeners();
	}
	
	private void addListeners() {
	    cancelText.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                
                cancel();
            }
        });
		loginViewButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				toLoginView();
			}
		});
		
		demoButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				demoMode();
			}
		});
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				login();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				cancel();
			}
		});
		
		logo.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
		    	Intent i = new Intent(LoginTabletActivity.this, DebugActivity.class);
		    	startActivity(i);
			}
		});
		username.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				
				username.setTextColor(getResources().getColor(hasFocus ? R.color.white : R.color.light_gray1));
			}
		});
		
		password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				
				password.setTextColor(getResources().getColor(hasFocus ? R.color.white : R.color.light_gray1));
			}
		});
		
		messageBody.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				return true;
			}
		});
	    signupName.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                
                signupName.setTextColor(getResources().getColor(hasFocus ? R.color.white : R.color.light_gray1));
            }
        });
	    signupEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                
                signupEmail.setTextColor(getResources().getColor(hasFocus ? R.color.white : R.color.light_gray1));
            }
        });
	    signupBank.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                
                signupBank.setTextColor(getResources().getColor(hasFocus ? R.color.white : R.color.light_gray1));
            }
        });
		messageTitle.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				return true;
			}
		});
		
		loginText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.setup_url)));
				startActivity(browserIntent);
			}	
		});
	    signupText.setOnClickListener(new OnClickListener() {
	            
	            public void onClick(View v) {

	                toSignupView();
	            }
	    });
	}
	
	private void setupAnimations() {
		
		inLeft = AnimationUtils.loadAnimation(this, R.anim.in_left);
		outRight = AnimationUtils.loadAnimation(this, R.anim.out_right);
		outLeft = AnimationUtils.loadAnimation(this, R.anim.out_left);
		inRight = AnimationUtils.loadAnimation(this, R.anim.in_right);
		
		inLeft.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {

				configureButtons(false);
			}
			
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation animation) {

				configureButtons(true);
			}
		});
		
		inRight.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {

				configureButtons(false);
			}
			
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation animation) {

				configureButtons(true);
			}
		});
	}
	
	private void configureFlipper() {

		viewFlipper.setInAnimation(inRight);
		viewFlipper.setOutAnimation(outLeft);
	}
	
	private void configureButtons(boolean enabled) {
		
		loginViewButton.setEnabled(enabled);
		demoButton.setEnabled(enabled);
		loginButton.setEnabled(enabled);
		cancelButton.setEnabled(enabled);
	}
	
	private void toLoginView() {
		
		viewFlipper.setDisplayedChild(1);
		viewFlipper.setInAnimation(outRight);
		viewFlipper.setOutAnimation(inLeft);
	}
	private void toSignupView() {
	    viewFlipper.setDisplayedChild(2);
	    viewFlipper.setInAnimation(outRight);
	    viewFlipper.setInAnimation(inLeft);
	}
	private void demoMode() {
		
		User.registerDemoUser();
		
		DialogUtils.showProgress(this, getString(R.string.demo_message));
		
		new AsyncTask<Void, Void, Boolean>() {
    		
			@Override
			protected Boolean doInBackground(Void... params) {

				DemoData.createDemoData();

				return true;
			}
    		
    		@Override
    		protected void onPostExecute(Boolean result) {

    			toDashboard();
    		}
			
		}.execute();
	}
	
	private void login() {
		
		if (failed) {
			
			resetLogin();
			
			return;
		}
		
		if (loginCheck()) {
	        
			authenticate();
	        
		} else {
			
			DialogUtils.alertDialog(getString(R.string.error_title), getString(R.string.error_login_incomplete), this, null);
		}
	}
	
	private void resetLogin() {
		
		toggleAnimations(true);
		
		loginButton.setText(getString(R.string.button_login));
		cancelButton.setText(getString(R.string.button_cancel));
		
		failed = false;
	}
	
	private boolean loginCheck() {
		
		return !username.getText().toString().equals("") && !password.getText().toString().equals("");
	}
	
	private void authenticate() {

		DialogUtils.showProgress(this, getString(R.string.loading));
		
		new AsyncTask<Void, Void, Boolean>() {
    		
			@Override
			protected Boolean doInBackground(Void... params) {

				DataBridge dataBridge = DataBridge.sharedInstance();
		        
		        try {
		        	
					dataBridge.authenticateUser(username.getText().toString(), password.getText().toString());
					
				} catch (Exception e) {
					
					Log.e(TAG, "Error Authenticating", e);
					return false;
				}

				return true;
			}
    		
    		@Override
    		protected void onPostExecute(Boolean result) {

    			DialogUtils.hideProgress();
    			
    			failed = !result;
				
    			if (!result) {
    				
    				messageTitle.setText(getString(R.string.error_login_failed));
    				messageBody.setText(getString(R.string.error_login_invalid));
							
					toggleAnimations(false);
					
					loginButton.setText(getString(R.string.button_return_login));
					cancelButton.setText(getString(R.string.button_setup));
    			
    			} else {
    				
    				EventBus eventBus = EventBus.getDefault();
    				eventBus.post(new EventMessage().new LoginEvent());
    				
    				toDashboard();
    			}
    		}
			
		}.execute();
	}
	
private void toggleAnimations(boolean reset) {
		
		int offset = (int) (.20 * UiUtils.getScreenMeasurements(this)[1]) * (reset ? -1 : 1);
		long duration = 750;
		long delay = reset ? 250 : 0;
		
		Animator.translateView(buttonView, new float[] {0, offset}, duration);
		Animator.fadeView(loginText, !reset, duration, delay);
		Animator.fadeView(credentialView, !reset, duration, delay);
		Animator.fadeView(username, !reset, duration, delay);
		Animator.fadeView(password, !reset, duration, delay);
		
		delay = reset ? 0 : 250;
		
		Animator.fadeView(messageTitle, reset, duration, delay);
		Animator.fadeView(messageBody, reset, duration, delay);
		
		Animator.startAnimations();
	}

	private void cancel() {
		
		if (failed) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.setup_url)));
			startActivity(browserIntent);
			
			resetLogin();
			
			return;
		}
		
		username.clearFocus();
		password.clearFocus();
		hideKeyboard();
		
		viewFlipper.setDisplayedChild(0);
		viewFlipper.setInAnimation(inRight);
		viewFlipper.setOutAnimation(outLeft);
	}
	
	private void hideKeyboard() {

    	InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(viewFlipper.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public String getActivityTitle() {
		return null;
	}
}
