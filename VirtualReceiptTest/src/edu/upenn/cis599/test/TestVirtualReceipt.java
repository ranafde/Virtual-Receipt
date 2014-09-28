package edu.upenn.cis599.test;

import com.jayway.android.robotium.solo.Solo;

//import junit.framework.TestCase;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import edu.upenn.cis599.eas499.VirtualReceiptActivity;

import edu.upenn.cis599.R;

public class TestVirtualReceipt extends
		ActivityInstrumentationTestCase2<VirtualReceiptActivity> {

	private Solo solo;

	public TestVirtualReceipt() {
		super(VirtualReceiptActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testModifyDescription() throws Exception {

		solo.clickOnText("Add a receipt");

		solo.clickOnButton("No");

		EditText mDescriptionText = (EditText) solo.getView(R.id.description);
		solo.clearEditText(mDescriptionText);
		solo.enterText(mDescriptionText,
				"buy a lot of time to finish the test!");

		boolean result1 = solo
				.searchText("buy a lot of time to finish the test!");
		assertTrue(result1);

		solo.clearEditText(mDescriptionText);
		solo.enterText(mDescriptionText, "Now you got it!");

		boolean result2 = solo.searchText("Now you got it!");
		
		assertTrue(result2);

		solo.clearEditText(mDescriptionText);

		boolean result3 = solo
				.searchText("buy a lot of time to finish the test!")
				|| solo.searchText("Now you got it!");
		assertFalse(result3);

		solo.goBackToActivity("VirtualReceiptActivity");
	}

	public void testModifyAmount() throws Exception {

		solo.clickOnText("Add a receipt");

		solo.clickOnButton("No");

		EditText mAmountText = (EditText) solo.getView(R.id.amount);
		solo.clearEditText(mAmountText);
		solo.enterText(mAmountText, "100");

		boolean result1 = solo.searchText("100");
		assertTrue(result1);

		solo.clearEditText(mAmountText);
		solo.enterText(mAmountText, "200");

		boolean result2 = solo.searchText("200");
		assertTrue(result2);

		solo.clearEditText(mAmountText);

		boolean result3 = solo.searchText("100") || solo.searchText("200");
		assertFalse(result3);

		solo.goBackToActivity("VirtualReceiptActivity");

	}

	public void testModifyDate() throws Exception {

		solo.clickOnText("Add a receipt");

		solo.clickOnButton("No");

		EditText mDateText = (EditText) solo.getView(R.id.date);
		solo.clearEditText(mDateText);
		solo.goBack();
	    solo.enterText(mDateText, "12/25/13");
	    solo.goBack();
		EditText mDescriptionText = (EditText) solo.getView(R.id.description);
		solo.clearEditText(mDescriptionText);
		solo.enterText(mDescriptionText, "Merry Christmas!!!");
		boolean result1 = solo.searchText("12/25/13");
		boolean result2 = solo.searchText("11/25/13");
		 
		assertTrue(result1);
		assertFalse(result2);
		 
	    solo.clearEditText(mDateText);
		solo.goBack();
		solo.enterText(mDateText, "01/01/14");
		solo.goBack();
		solo.clearEditText(mDescriptionText);
		solo.enterText(mDescriptionText, "Happy New Year!!!");
		boolean result3 = solo.searchText("12/25/13");
		boolean result4 = solo.searchText("01/01/14");
		assertFalse(result3);
		assertTrue(result4);
		 
		solo.clearEditText(mDateText);
		solo.clearEditText(mDescriptionText);
		solo.goBackToActivity("VirtualReceiptActivity");
	}

	 public void testModifyCategory() throws Exception{
		 
		 solo.clickOnText("Add a receipt");
		 solo.clickOnButton("No");
		 solo.clickOnText("Education");
		 solo.clickInList(4);
		 boolean result1 = solo.searchText("Rent");
		 assertTrue(result1);
		 
		 solo.clickOnText("Rent");
		 solo.clickInList(4);
		 boolean result2 = solo.searchText("Bill");
		 assertTrue(result2);
	 }
	 
	 public void testAddReceipt() throws Exception{
		 
		 solo.clickOnText("Add a receipt");
		 solo.clickOnButton("No");
		 EditText mDescriptionText = (EditText) solo.getView(R.id.description);
		 EditText mAmountText = (EditText) solo.getView(R.id.amount);
		 EditText mDateText = (EditText) solo.getView(R.id.date);
		 
		 solo.clearEditText(mDescriptionText);
		 solo.enterText(mDescriptionText, "The tuition is a pain!");
		 
		 solo.clearEditText(mAmountText);
		 solo.enterText(mAmountText, "99999");
		 
		 solo.clearEditText(mDateText);
		 solo.goBack();
		 solo.enterText(mDateText, "12/25/13");
		 solo.goBack();
		 
		 solo.clickOnText("Education");
		 solo.clickInList(1);
		 
		 solo.clickOnText("Cash");
		 
		 solo.clickOnButton("Save");
		 
		 solo.goBackToActivity("VirtualReceiptActivity");
		 
		 solo.clickOnText("View receipts");
		 
		 boolean result1 = solo.searchText("The tuition is a pain!");
		 
		 assertTrue(result1);
		 
		 solo.clickOnText("The tuition is a pain!");
		 
		 boolean result2 = solo.searchText("The tuition is a pain!") && solo.searchText("99999") && 
		 solo.searchText("12-09-2013") && solo.searchText("Education") && solo.searchText("Cash");
		 
		 assertTrue(result2);
		 
		 solo.clickOnButton("Delete");
		 solo.clickOnButton("Yes");
		 
		 solo.goBackToActivity("VirtualReceiptActivity");
		 
		 solo.clickOnText("View receipts");
		 
		 boolean result3 = solo.searchText("The tuition is  a pain!");
		 
		 assertFalse(result3);
	 }
	 
		//create a database with 3 receipts then compare them with the expected charts
		public void testSpendStat() throws Exception {
			//add new receipt1
			 solo.clickOnText("Add a receipt");
			 solo.clickOnButton("No");
			 EditText mDescriptionText1 = (EditText) solo.getView(R.id.description);
			 EditText mAmountText1 = (EditText) solo.getView(R.id.amount);
			 EditText mDateText1 = (EditText) solo.getView(R.id.date);
			 
			 solo.clearEditText(mDescriptionText1);
			 solo.enterText(mDescriptionText1, "Test1");
			 
			 solo.clearEditText(mAmountText1);
			 solo.enterText(mAmountText1, "100");
			 
			 solo.clearEditText(mDateText1);
			 solo.goBack();
			 solo.enterText(mDateText1, "12/01/13");
			 solo.goBack();
			 
			 solo.clickOnText("Education");
			 solo.clickInList(1);
			 
			 solo.clickOnText("Cash");
			 
			 solo.clickOnButton("Save");
			 
			//add new receipt2
			 solo.clickOnText("Add a receipt");
			 solo.clickOnButton("No");

			 EditText mDescriptionText2 = (EditText) solo.getView(R.id.description);
			 EditText mAmountText2 = (EditText) solo.getView(R.id.amount);
			 EditText mDateText2 = (EditText) solo.getView(R.id.date);
			 solo.clearEditText(mDescriptionText2);
			 solo.enterText(mDescriptionText2, "Test2");
			 
			 solo.clearEditText(mAmountText2);
			 solo.enterText(mAmountText2, "200");
			 
			 solo.clearEditText(mDateText2);
			 solo.goBack();
			 solo.enterText(mDateText2, "12/03/13");
			 solo.goBack();
			 
			 solo.clickOnText("Education");
			 solo.clickInList(3);
			 
			 solo.clickOnText("Credit");
			 
			 solo.clickOnButton("Save");
			 
			//add new receipt3
			 solo.clickOnText("Add a receipt");
			 solo.clickOnButton("No");
			 EditText mDescriptionText3 = (EditText) solo.getView(R.id.description);
			 EditText mAmountText3 = (EditText) solo.getView(R.id.amount);
			 EditText mDateText3 = (EditText) solo.getView(R.id.date);
			 
			 solo.clearEditText(mDescriptionText3);
			 solo.enterText(mDescriptionText3, "Test3");
			 
			 solo.clearEditText(mAmountText3);
			 solo.enterText(mAmountText3, "300");
			 
			 solo.clearEditText(mDateText3);
			 solo.goBack();
			 solo.enterText(mDateText3, "12/09/13");
			 solo.goBack();
			 
			 solo.clickOnText("Clothing");
			 solo.clickInList(4);
			 
			 solo.clickOnText("Debit");
			 
			 solo.clickOnButton("Save");
			 
			 //show charts
			 solo.clickOnText("View spending statistics");
			 solo.clickOnText("Category Spending Analyzer");
			 solo.sleep(2500);
			 solo.goBack();
			 solo.clickOnText("Monthly Spending");
			 solo.sleep(2500);
			 solo.goBack();
			 solo.clickOnText("Payment Type Analyzer");
			 solo.sleep(2500);
			 solo.goBack();
		}
	  
//	 public void testTakePic() throws Exception {
//		 solo.clickOnText("Add a receipt");
//		 solo.clickOnButton("Yes");
//		 solo.waitForText("Payment Method");
//
//		 EditText mDescriptionText = (EditText) solo.getView(R.id.description);
//		 solo.clearEditText(mDescriptionText);
//		 solo.enterText(mDescriptionText, "Change to Ensure the valid input :)");
//	
//	     EditText mAmountText = (EditText) solo.getView(R.id.amount);
//		 solo.clearEditText(mAmountText);
//		 solo.enterText(mAmountText, "100");
//		 
//		 
//		 solo.clickOnText("Education");
//		 solo.clickInList(2);
//			 
//		 solo.clickOnText("Credit");
//			 
//		 solo.clickOnButton("Save");
//		 solo.clickOnText("No");
//		 
//		 //check if the image is successfully saved
//		 solo.goBackToActivity("VirtualReceiptActivity");
//		 solo.clickOnText("View receipts");
//		 boolean result1 = solo.searchText("Change to Ensure the valid input :)");
//		 solo.clickOnText("Change to Ensure the valid input :)");
//		 solo.clickOnText("delete");
//		 solo.clickOnText("Yes");
//		 assertTrue(result1);	
//		 }	
	
//	 public void testDropbox() throws Exception{
//		 
//		 solo.clickOnText("Sync with dropbox");
//		 solo.clickOnText("Link with Dropbox");
//		 solo.sleep(4000);
//		 
//		 if (solo.searchText("Unlink from Dropbox")){
//			 solo.clickOnText("Sync From Dropbox");
//			 solo.sleep(4000);
//			 solo.goBackToActivity("VirtualReceiptActivity");
//			 solo.clickOnText("View receipts");
//			 boolean result1 = solo.searchText("Test");
//			 assertTrue(result1);
//			 
//			 //delete
//			 solo.clickOnText("Test");
//			 solo.clickOnButton("Delete");
//			 solo.clickOnButton("Yes");
//			 solo.goBackToActivity("VirtualReceiptActivity");
//			 
//			 //add new receipt
//			 solo.clickOnText("Add a receipt");
//			 solo.clickOnButton("No");
//			 EditText mDescriptionText = (EditText) solo.getView(R.id.description);
//			 EditText mAmountText = (EditText) solo.getView(R.id.amount);
//			 EditText mDateText = (EditText) solo.getView(R.id.date);
//			 
//			 solo.clearEditText(mDescriptionText);
//			 solo.enterText(mDescriptionText, "The tuition is a pain!");
//			 
//			 solo.clearEditText(mAmountText);
//			 solo.enterText(mAmountText, "99999");
//			 
//			 solo.clearEditText(mDateText);
//			 solo.goBack();
//			 solo.enterText(mDateText, "12/25/13");
//			 solo.goBack();
//			 
//			 solo.clickOnText("Education");
//			 solo.clickInList(1);
//			 
//			 solo.clickOnText("Cash");
//			 
//			 solo.clickOnButton("Save");
//			 
//			 //sync to dropbox
//			 solo.clickOnText("Sync with dropbox");
//			 solo.clickOnText("Sync To Dropbox");
//			 //solo.sleep(4000);
//		 }
//	}
		 
	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
}

