package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;

import java.time.format.DateTimeFormatter;

public class TransferResp {
	
	private static final DateTimeFormatter ISO_INSTANT_NO_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private long id;
	private double amount;
	private String time;
	private String type;
	private UserResp buyer;
	private UserResp seller;
	private ItemResp item;
	
	public TransferResp(Transfer transfer) {
		id = transfer.getId();
		amount = transfer.getAmount().doubleValue();
		time = transfer.getCreatedAt() != null ? transfer.getCreatedAt().format(ISO_INSTANT_NO_MILLIS) : "";
		type = transfer.getClass().getSimpleName().replace("Transfer", "").toUpperCase();
		if (transfer instanceof PurchaseTransfer) {
			PurchaseTransfer pt = (PurchaseTransfer) transfer;
			buyer = new UserResp(pt.getBuyer());
			seller = new UserResp(pt.getSeller());
			item = new ItemResp(pt.getItem());
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UserResp getBuyer() {
		return buyer;
	}

	public void setBuyer(UserResp buyer) {
		this.buyer = buyer;
	}

	public UserResp getSeller() {
		return seller;
	}

	public void setSeller(UserResp seller) {
		this.seller = seller;
	}

	public ItemResp getItem() {
		return item;
	}

	public void setItem(ItemResp item) {
		this.item = item;
	}
}
