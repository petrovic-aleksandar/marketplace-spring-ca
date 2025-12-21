package me.aco.marketplace_spring_ca.infrastructure;

import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RepositoryTests {

    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private JpaItemRepository itemRepository;
    @Autowired
    private JpaImageRepository imageRepository;
    @Autowired
    private JpaTransferRepository transferRepository;
    @Autowired
    private JpaItemTypeRepository itemTypeRepository;

    @Test
    void contextLoads() {
        assertNotNull(userRepository, "UserRepository should be autowired and not null");
        assertNotNull(itemRepository, "ItemRepository should be autowired and not null");
        assertNotNull(itemTypeRepository, "ItemTypeRepository should be autowired and not null");
        assertNotNull(imageRepository, "ImageRepository should be autowired and not null");
        assertNotNull(transferRepository, "TransferRepository should be autowired and not null");
    }

    @Test
    void persistUser() {
        // Arrange
        User newUser = new User(
                null,
                "testuser",
                "password123",
                "Test User",
                "test@example.com",
                "555-1234",
                new BigDecimal("100.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        assertNotNull(savedUser.getId(), "User ID should not be null after saving");
        assertEquals("testuser", savedUser.getUsername(), "Username should match");
        assertEquals("Test User", savedUser.getName(), "Name should match");
        assertEquals("test@example.com", savedUser.getEmail(), "Email should match");
        assertEquals(new BigDecimal("100.00"), savedUser.getBalance(), "Balance should match");
        assertEquals(UserRole.USER, savedUser.getRole(), "Role should match");
        assertTrue(savedUser.isActive(), "User should be active");

        // Verify persistence by retrieving from repository
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser, "User should be retrievable from repository");
        assertEquals("testuser", retrievedUser.getUsername(), "Retrieved user should have correct username");
    }

    @Test
    void persistItemType() {
        // Arrange
        ItemType newItemType = new ItemType(
                null,
                "Electronics",
                "Electronic devices and gadgets",
                "electronics.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        ItemType savedItemType = itemTypeRepository.save(newItemType);

        // Assert
        assertNotNull(savedItemType.getId(), "ItemType ID should not be null after saving");
        assertEquals("Electronics", savedItemType.getName(), "ItemType name should match");
        assertEquals("Electronic devices and gadgets", savedItemType.getDescription(), "ItemType description should match");

        // Verify persistence
        ItemType retrievedItemType = itemTypeRepository.findById(savedItemType.getId()).orElse(null);
        assertNotNull(retrievedItemType, "ItemType should be retrievable from repository");
        assertEquals("Electronics", retrievedItemType.getName(), "Retrieved ItemType should have correct name");
    }

    @Test
    void persistItem() {
        // Arrange - Create and save a seller user and item type first
        User seller = new User(
                null,
                "seller",
                "password",
                "John Seller",
                "seller@example.com",
                "555-5555",
                new BigDecimal("500.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );
        User savedSeller = userRepository.save(seller);

        ItemType itemType = new ItemType(
                null,
                "Books",
                "Books and media",
                "books.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ItemType savedItemType = itemTypeRepository.save(itemType);

        Item newItem = new Item();
        newItem.setName("Java Programming");
        newItem.setDescription("Learn Java from basics to advanced");
        newItem.setPrice(new BigDecimal("29.99"));
        newItem.setType(savedItemType);
        newItem.setSeller(savedSeller);
        newItem.setActive(true);
        newItem.setDeleted(false);
        newItem.setUpdatedAt(LocalDateTime.now());

        // Act
        Item savedItem = itemRepository.save(newItem);

        // Assert
        assertNotNull(savedItem.getId(), "Item ID should not be null after saving");
        assertEquals("Java Programming", savedItem.getName(), "Item name should match");
        assertEquals(new BigDecimal("29.99"), savedItem.getPrice(), "Item price should match");
        assertTrue(savedItem.isActive(), "Item should be active");
        assertFalse(savedItem.isDeleted(), "Item should not be deleted");

        // Verify persistence
        Item retrievedItem = itemRepository.findById(savedItem.getId()).orElse(null);
        assertNotNull(retrievedItem, "Item should be retrievable from repository");
        assertEquals("Java Programming", retrievedItem.getName(), "Retrieved item should have correct name");
    }

    @Test
    void persistImage() {
        // Arrange - Create and save a seller, item type, and item first
        User seller = new User(
                null,
                "seller2",
                "password",
                "Jane Seller",
                "jane@example.com",
                "555-6666",
                new BigDecimal("500.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );
        User savedSeller = userRepository.save(seller);

        ItemType itemType = new ItemType(
                null,
                "Clothing",
                "Clothing items",
                "clothing.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ItemType savedItemType = itemTypeRepository.save(itemType);

        Item item = new Item();
        item.setName("Blue T-Shirt");
        item.setDescription("Cotton blue t-shirt");
        item.setPrice(new BigDecimal("19.99"));
        item.setType(savedItemType);
        item.setSeller(savedSeller);
        item.setActive(true);
        item.setDeleted(false);
        item.setUpdatedAt(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);

        Image newImage = new Image(
                null,
                "blue_tshirt_front.jpg",
                savedItem,
                true
        );

        // Act
        Image savedImage = imageRepository.save(newImage);

        // Assert
        assertNotNull(savedImage.getId(), "Image ID should not be null after saving");
        assertEquals("blue_tshirt_front.jpg", savedImage.getPath(), "Image path should match");
        assertTrue(savedImage.isFront(), "Image should be marked as front");

        // Verify persistence
        Image retrievedImage = imageRepository.findById(savedImage.getId()).orElse(null);
        assertNotNull(retrievedImage, "Image should be retrievable from repository");
        assertEquals("blue_tshirt_front.jpg", retrievedImage.getPath(), "Retrieved image should have correct path");
    }

    @Test
    void persistTransfer() {
        // Arrange - Create and save buyer, seller, item type, and item
        User buyer = new User(
                null,
                "buyer",
                "password",
                "Alice Buyer",
                "buyer@example.com",
                "555-7777",
                new BigDecimal("1000.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );
        User savedBuyer = userRepository.save(buyer);

        User seller = new User(
                null,
                "seller3",
                "password",
                "Bob Seller",
                "seller3@example.com",
                "555-8888",
                new BigDecimal("500.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );
        User savedSeller = userRepository.save(seller);

        ItemType itemType = new ItemType(
                null,
                "Electronics",
                "Electronic items",
                "electronics.jpg",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ItemType savedItemType = itemTypeRepository.save(itemType);

        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("High-performance laptop");
        item.setPrice(new BigDecimal("999.99"));
        item.setType(savedItemType);
        item.setSeller(savedSeller);
        item.setActive(true);
        item.setDeleted(false);
        item.setUpdatedAt(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);

        PurchaseTransfer newTransfer = new PurchaseTransfer(
                null,
                savedItem.getPrice(),
                savedBuyer,
                savedSeller,
                savedItem
        );

        // Act
        PurchaseTransfer savedTransfer = (PurchaseTransfer) transferRepository.save(newTransfer);

        // Assert
        assertNotNull(savedTransfer.getId(), "Transfer ID should not be null after saving");
        assertEquals(new BigDecimal("999.99"), savedTransfer.getAmount(), "Transfer amount should match");
        assertEquals(savedBuyer.getId(), savedTransfer.getBuyer().getId(), "Buyer should match");
        assertEquals(savedSeller.getId(), savedTransfer.getSeller().getId(), "Seller should match");
        assertEquals(savedItem.getId(), savedTransfer.getItem().getId(), "Item should match");

        // Verify persistence
        PurchaseTransfer retrievedTransfer = (PurchaseTransfer) transferRepository.findById(savedTransfer.getId()).orElse(null);
        assertNotNull(retrievedTransfer, "Transfer should be retrievable from repository");
        assertEquals(new BigDecimal("999.99"), retrievedTransfer.getAmount(), "Retrieved transfer should have correct amount");
    }
}
