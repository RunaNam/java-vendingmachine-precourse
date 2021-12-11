package vendingmachine.controller;

import static constants.ProductConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vendingmachine.domain.Coin;
import vendingmachine.domain.User;
import vendingmachine.domain.VendingMachineMoney;
import vendingmachine.domain.VendingMachineProduct;
import vendingmachine.domain.VendingMachineProducts;
import vendingmachine.view.InputView;
import vendingmachine.view.OutputView;

public class VendingMachineController {
	private final VendingMachineMoney vendingMachineMoney = new VendingMachineMoney();
	private VendingMachineProducts vendingMachineProducts;
	private User user;

	public void start() {
		saveVendingMachineMoney();
		OutputView.printVendingMachineMoney(vendingMachineMoney.getCoins());
		saveProducts();
		saveUser();
		vendingMachineWork();
	}

	private void saveUser() {
		Integer userMoney = InputView.getUserMoney();
		user = new User(userMoney);
		OutputView.printNewLine();
	}

	private void saveProducts() {
		List<List<String>> inputProducts = InputView.getProducts();
		List<VendingMachineProduct> products = new ArrayList<>();

		for (List<String> product : inputProducts) {
			String name = product.get(NAME_IDX);
			Integer price = Integer.parseInt(product.get(PRICE_IDX));
			Integer amount = Integer.parseInt(product.get(AMOUNT_IDX));
			products.add(new VendingMachineProduct(name, price, amount));
		}
		vendingMachineProducts = new VendingMachineProducts(products);
		OutputView.printNewLine();
	}

	private void saveVendingMachineMoney() {
		int vendingMachineInputMoney = InputView.getVendingMachineMoney();
		vendingMachineMoney.moneyToCoins(vendingMachineInputMoney);
		OutputView.printNewLine();
	}

	private void vendingMachineWork() {
		while (canBuyProduct()) {
			OutputView.printUserInputMoney(user);
			buyProduct();
		}
		getChange();
	}

	private void buyProduct() {
		String productName = InputView.getProductName();
		try {
			VendingMachineProduct product = vendingMachineProducts.findName(productName);
			vendingMachineProducts.buyProduct(productName);
			user.buyProduct(product.getPrice());
		} catch (IllegalArgumentException e) {
			OutputView.printError(e.getMessage());
		}
	}

	private boolean canBuyProduct() {
		return vendingMachineProducts.hasProduct() && user.isEnoughMoney(vendingMachineProducts.getLowestPrice());
	}

	private void getChange() {
		OutputView.printUserInputMoney(user);
		Map<Coin, Integer> changes = vendingMachineMoney.getChanges(user.getMoney());
		OutputView.printChanges(changes);
	}
}
