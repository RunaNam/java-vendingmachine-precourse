package vendingmachine.controller;

import static constants.ProductConstants.*;
import static constants.VendingMachineConstants.*;

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
		try {
			Integer userMoney = InputView.getUserMoney(vendingMachineProducts.getLowestPrice());
			user = new User(userMoney);
			OutputView.printNewLine();
		} catch (IllegalArgumentException e) {
			OutputView.printError(e.getMessage());
			saveUser();
		}
	}

	private void saveProducts() {
		try {
			vendingMachineProducts = new VendingMachineProducts(getProducts());
			OutputView.printNewLine();
		} catch (IllegalArgumentException e) {
			OutputView.printError(e.getMessage());
		}
	}

	private List<VendingMachineProduct> getProducts() {
		List<VendingMachineProduct> products = new ArrayList<>();
		for (List<String> productInfo : InputView.getProducts()) {
			VendingMachineProduct product = getProduct(productInfo);
			if (products.contains(product)) {
				throw new IllegalArgumentException(DUPLICATE_PRODUCT_ERROR);
			}
			products.add(product);
		}
		return products;
	}

	private VendingMachineProduct getProduct(List<String> productInfo) {
		String name = productInfo.get(NAME_IDX);
		Integer price = Integer.parseInt(productInfo.get(PRICE_IDX));
		Integer amount = Integer.parseInt(productInfo.get(AMOUNT_IDX));
		return new VendingMachineProduct(name, price, amount);
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
