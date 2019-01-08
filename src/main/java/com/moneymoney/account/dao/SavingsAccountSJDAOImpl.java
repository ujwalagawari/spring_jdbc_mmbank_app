/**
 * 
 */
package com.moneymoney.account.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.moneymoney.account.util.DBUtil;
import com.moneymoney.pojo.account.SavingsAccount;
import com.moneymoney.pojo.account.SavingsAccountMapper;
import com.moneymoney.pojo.exception.AccountNotFoundException;

/**
 * @author ugawari
 *
 */
@Repository
@Primary
public class SavingsAccountSJDAOImpl implements SavingsAccountDAO{

	private Logger logger = Logger.getLogger(SavingsAccount.class.getName());
	
	@Autowired
	private JdbcTemplate template;
	
	@Override
	public SavingsAccount createNewAccount(SavingsAccount account) {
		logger.info("Inside create new account.");
		template.update("INSERT INTO ACCOUNT VALUES(?,?,?,?,?,?)", new Object[] {
				account.getBankAccount().getAccountNumber(),
				account.getBankAccount().getAccountHolderName(),
				account.getBankAccount().getAccountBalance(),
				account.isSalary(),
				null, "SA"
		});
		logger.info("New account is created.");
		return account;
	}

	@Override
	public SavingsAccount updateAccount(SavingsAccount account, int accountNumber)throws ClassNotFoundException, SQLException, AccountNotFoundException {
		logger.info("Inside update account.");
		template.update("UPDATE ACCOUNT SET salary=?, account_hn=? where account_id=?",
				new Object[] {account.isSalary(),
						account.getBankAccount().getAccountHolderName(),
						accountNumber
				});
		logger.info("Account updated.");
		return account;
	}

	@Override
	public SavingsAccount getAccountById(int accountNumber) {
		logger.info("Inside get account by id");
		return template.queryForObject("SELECT * FROM account where account_id=?" ,
				new Object[] {accountNumber}, new SavingsAccountMapper());
	}

	@Override
	public SavingsAccount deleteAccount(int accountNumber)throws ClassNotFoundException, SQLException, AccountNotFoundException {
		logger.info("Inside delete account by id");
		template.update("DELETE FROM ACCOUNT WHERE ACCOUNT_ID=?", accountNumber);
		logger.info("Account deleted successfully");
		return getAccountById(accountNumber);
	}

	@Override
	public List<SavingsAccount> getAllSavingsAccount() {
		logger.info("Inside get all saving accounts");
		return template.query("SELECT * FROM ACCOUNT" , new SavingsAccountMapper());
	}

	@Override
	public void updateBalance(int accountNumber, double currentBalance) {
		logger.info("Update balance by id");
		template.update("UPDATE ACCOUNT SET account_balance=? where account_id=?", 
				new Object[] {
						currentBalance, accountNumber
				});
	}

	@Override
	public void commit() throws SQLException {
		//DBUtil.commit();
	}

	@Override
	public double getCurrentBalance(int accountNumber) {		
		/*
		 * String currentBalance = (String) template.queryForObject(
		 * "SELECT ACCOUNT_BALANCE FROM ACCOUNT WHERE ACCOUNT_ID=?", new Object[] {
		 * accountNumber }, String.class); return Double.parseDouble(currentBalance);
		 */
		logger.info("get account balance by account number");
		return template.queryForObject("SELECT ACCOUNT_BALANCE FROM ACCOUNT WHERE ACCOUNT_ID=?", new Object[] {accountNumber }, Double.class); 
	}

	@Override
	public List<SavingsAccount> getAccountsByHolderName(String holderName) {
		logger.info("get accounts by account holder name");
		String sql = "SELECT * FROM ACCOUNT WHERE account_hn=?";
		return template.query(sql, new Object[] {holderName}, new SavingsAccountMapper());
	}

	@Override
	public List<SavingsAccount> getAccountsBetweenMinMaxAccountBal(Double minBalance, Double maxBalance) {
		logger.info("get accounts between minimum and maximum account balance");
		List<SavingsAccount> listOfAccounts= template.query("SELECT * FROM ACCOUNT WHERE account_balance between ? and ?", 
				new Object[] {minBalance, maxBalance},
				new SavingsAccountMapper()
		);
		logger.info("Return list of accounts between minimum and maximum account balance");
		return listOfAccounts;
	}

}
