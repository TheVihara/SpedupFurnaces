package me.gorenjec.spedupfurnaces.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnegative;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Rysefoxx | Rysefoxx#6772
 * @since 1/18/2022
 */
public class ItemBuilder {

    private final HashMap<String, Object> data = new HashMap<>();
    private ItemStack itemStack;
    private String displayName;
    private Material material;
    private int amount;
    private ItemMeta itemMeta;
    private boolean glow;
    private Color color;
    private int damage;
    private String base64;
    private String skullName;
    private UUID skullOwner;
    private List<ItemFlag> flags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();


    /**
     * Creates a new ItemBuilder object based on an existing ItemStack.
     *
     * @param itemStack The already existing ItemStack
     * @throws IllegalArgumentException If an invalid ItemStack is passed.
     */
    public ItemBuilder(ItemStack itemStack) throws IllegalArgumentException {
        Validate.notNull(itemStack, "The itemStack must not be null");

        this.itemStack = itemStack;
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta.hasLore()) {
            this.lore = this.itemMeta.getLore();
        }
        if (this.itemMeta.hasEnchants()) {
            this.enchantments = this.itemMeta.getEnchants();
        }
        if (this.itemMeta.hasDisplayName()) {
            this.displayName = this.itemMeta.getDisplayName();
        }
        if (!this.itemMeta.getItemFlags().isEmpty()) {
            this.flags = Arrays.asList(this.itemMeta.getItemFlags().toArray(new ItemFlag[0]));
        }
    }

    /**
     * Creates a new ItemBuilder object based on the material type and amount
     *
     * @param material What kind of material the item should be in the end.
     */
    public ItemBuilder(Material material, Number number) {
        Validate.notNull(material, "The material must not be null");
        Validate.notNull(number, "The number must not be null");

        int amount = number.intValue();

        if (amount > this.material.getMaxStackSize() || amount > 64) {
            amount = this.material.getMaxStackSize();
        }
        if (amount < 0) {
            amount = 1;
        }

        this.material = material;
        this.amount = amount;
    }

    /**
     * Creates a new ItemBuilder object based on the material type
     *
     * @param material What kind of material the item should be in the end.
     * @throws IllegalArgumentException If the material is inappropriate.
     */
    public ItemBuilder(Material material) throws IllegalArgumentException {
        Validate.notNull(material, "The material must not be null");
        this.material = material;
        this.amount = 1;
    }

    /**
     * Changes the amount of the ItemStack
     *
     * @param number The new amount
     * @return The new ItemBuilder object with which further changes can be made.
     */
    public ItemBuilder amount(Number number) {
        int amount = number.intValue();
        if (amount > this.material.getMaxStackSize() || amount > 64) {
            amount = this.material.getMaxStackSize();
        }
        if (amount < 0) {
            amount = 1;
        }

        this.amount = amount;
        return this;
    }

    /**
     * Changes the skull design
     *
     * @param uuid uuid from player
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the UUID is inappropriate.
     * @throws IllegalStateException    If the material is not a skull.
     */
    public ItemBuilder skullFromUuid(UUID uuid) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(uuid, "The uuid must not be null");

//        if (this.material != Material.PLAYER_HEAD) {
//            throw new IllegalStateException("The material must be a PLAYER_HEAD.");
//        }

        this.skullOwner = uuid;
        return this;
    }


    /**
     * Create a custom skull based on base64
     *
     * @param base64 Skin in base64
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the string is inappropriate.
     * @throws IllegalStateException    If the material is not a skull.
     */
    public ItemBuilder skullFromBase64(String base64) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(base64, "The base64 must not be null");

//        if (this.material != Material.PLAYER_HEAD) {
//            throw new IllegalStateException("The material must be a PLAYER_HEAD.");
//        }

        this.base64 = base64;
        return this;
    }

    /**
     * Create a custom skull based on base64
     *
     * @param name playername
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the string is inappropriate.
     * @throws IllegalStateException    If the material is not a skull.
     */
    public ItemBuilder skullFromName(String name) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(name, "The name must not be null");

//        if (this.material != Material.PLAYER_HEAD) {
//            throw new IllegalStateException("The material must be a PLAYER_HEAD.");
//        }

        this.skullName = name;
        return this;
    }

    /**
     * Dyes the leather equipment
     *
     * @param color The color
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalStateException    If the material is not leather type.
     * @throws IllegalArgumentException If the color is inappropriate.
     */
    public ItemBuilder color(Color color) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(color, "The color must not be null");
        if (!this.material.name().toLowerCase(Locale.ROOT).split("_")[0].equalsIgnoreCase("leather")) {
            throw new IllegalStateException("The material must be a leather equipment part.");
        }
        this.color = color;
        return this;
    }

    /**
     * Ensures that the item glows.
     *
     * @return The new ItemBuilder object with which further changes can be made.
     */
    public ItemBuilder glow() {
        this.glow = true;
        return this;
    }

    /**
     * Ensures that the item glows.
     *
     * @return The new ItemBuilder object with which further changes can be made.
     */
    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    /**
     * Adds a custom display name to the item.
     *
     * @param displayName    The display name
     * @param translateCodes Should the translation code '&' be translated as well?
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the display name is inappropriate.
     */
    public ItemBuilder displayName(String displayName, boolean translateCodes) throws IllegalArgumentException {
        Validate.notNull(displayName, "The displayName must not be null");
        if (translateCodes) displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.displayName = displayName;
        return this;
    }

    /**
     * Adds a custom display name to the item.
     *
     * @param displayName The display name
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the display name is inappropriate.
     */
    public ItemBuilder displayName(String displayName) throws IllegalArgumentException {
        Validate.notNull(displayName, "The displayName must not be null");
        this.displayName = displayName;
        return this;
    }

    /**
     * Adds data to the item.
     *
     * @param key   The data key with which the value can be obtained.
     * @param value The data value that can be obtained via the data key.
     * @return The new ItemBuilder object with which further changes can be made.
     */
    public ItemBuilder data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * Adds a flag to the item to hide certain values.
     *
     * @param flags All flags that should be added to the item.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the flags are inappropriate.
     */
    public ItemBuilder flags(List<ItemFlag> flags) throws IllegalArgumentException {
        Validate.notNull(flags, "The flags must not be null");
        this.flags = flags;
        return this;
    }

    /**
     * Adds a flag to the item to hide certain values.
     *
     * @param flags All flags that should be added to the item.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the flags are inappropriate.
     */
    public ItemBuilder flags(ItemFlag... flags) throws IllegalArgumentException {
        Validate.notNull(flags, "The flags must not be null");
        this.flags = Arrays.asList(flags);
        return this;
    }

    /**
     * Adds a single flag to the item.
     *
     * @param flag The flag to be added to the item.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the flag is inappropriate.
     */
    public ItemBuilder flag(ItemFlag flag) throws IllegalArgumentException {
        Validate.notNull(flag, "The flag must not be null");
        this.flags.add(flag);
        return this;
    }

    /**
     * Clears the lore
     *
     * @return The new ItemBuilder object with which further changes can be made.
     */
    private ItemBuilder clearLore() {
        if (this.itemMeta.getLore() == null) return this;
        this.itemMeta.getLore().clear();
        return this;
    }

    /**
     * Adds several lines of description to the item.
     *
     * @param lines The description.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the lines are inappropriate.
     */
    public ItemBuilder lore(String... lines) throws IllegalArgumentException {
        Validate.notNull(lines, "The lines must not be null");
        this.lore.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * Adds several lines of description to the item with translating.
     *
     * @param lines The description.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the lines are inappropriate.
     */
    public ItemBuilder lore(boolean translateCodes, String... lines) throws IllegalArgumentException {
        Validate.notNull(lines, "The lines must not be null");

        if (!translateCodes) {
            this.lore = Arrays.stream(lines).collect(Collectors.toList());
            return this;
        }

        List<String> finalList = new ArrayList<>();

        for (String line : lines) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            finalList.add(line);
        }

        this.lore = finalList;
        return this;
    }

    /**
     * Adds several lines of description to the item.
     *
     * @param lines The description.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the lines are inappropriate.
     */
    public ItemBuilder lore(List<String> lines) throws IllegalArgumentException {
        Validate.notNull(lines, "The lines must not be null");
        this.lore = lines;
        return this;
    }


    /**
     * Adds several lines of description to the item with & translate.
     *
     * @param lines The description.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the lines are inappropriate.
     */
    public ItemBuilder lore(List<String> lines, boolean translateCodes) throws IllegalArgumentException {
        Validate.notNull(lines, "The lines must not be null");

        if (!translateCodes) {
            this.lore = lines;
            return this;
        }

        List<String> finalList = new ArrayList<>();

        for (String line : lines) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            finalList.add(line);
        }

        this.lore = finalList;
        return this;
    }


    /**
     * Adds a single row to the ItemStack.
     *
     * @param line           The line.
     * @param translateCodes Should the line be translated with the translation code '&'.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the line is inappropriate.
     */
    public ItemBuilder appendLine(String line, boolean translateCodes) throws IllegalArgumentException {
        Validate.notNull(line, "The line must not be null");
        if (translateCodes) line = ChatColor.translateAlternateColorCodes('&', line);

        this.lore.add(line);
        return this;
    }

    /**
     * Adds a single row to the ItemStack.
     *
     * @param line The line.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the line is inappropriate.
     */
    public ItemBuilder appendLine(String line) throws IllegalArgumentException {
        Validate.notNull(line, "The line must not be null");
        this.lore.add(line);
        return this;
    }

    /**
     * Clears all enchantments
     *
     * @return The new ItemBuilder object with which further changes can be made.
     */
    public ItemBuilder clearEnchants() {
        if (!this.itemMeta.hasEnchants()) return this;

        this.itemMeta.getEnchants().keySet().forEach(enchantment -> this.itemMeta.removeEnchant(enchantment));
        return this;
    }

    /**
     * Inflicts damage to the item.
     *
     * @param damage How much damage the item receives
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the damage is less than or equal to 0.
     */
    public ItemBuilder damage(@Nonnegative int damage) throws IllegalArgumentException {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be at least 1.");
        }

        this.damage = damage;
        return this;
    }

    /**
     * Remove a specific enchantment
     *
     * @param enchantment The enchantment to be removed.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the enchantment is inappropriate.
     */
    public ItemBuilder removeEnchant(Enchantment enchantment) throws IllegalArgumentException {
        Validate.notNull(enchantment, "The enchantment must not be null");
        this.itemMeta.removeEnchant(enchantment);
        return this;
    }

    /**
     * Remove multiple enchantments
     *
     * @param enchantments The enchantments to be removed.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the enchantments are inappropriate.
     */
    public ItemBuilder removeEnchant(Enchantment... enchantments) throws IllegalArgumentException {
        Validate.notNull(enchantments, "The enchantments must not be null");
        for (Enchantment enchantment : enchantments) {
            this.itemMeta.removeEnchant(enchantment);
        }
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment What kind of enchantment should be added.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the enchantment is inappropriate.
     */
    public ItemBuilder enchant(Enchantment enchantment) throws IllegalArgumentException {
        Validate.notNull(enchantment, "The enchantment must not be null");
        this.enchantments.put(enchantment, 1);
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment What kind of enchantment should be added.
     * @param level       What level should the enchantment be?
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If one of the passed values is inappropriate.
     */
    public ItemBuilder enchant(Enchantment enchantment, @Nonnegative int level) throws IllegalArgumentException {
        Validate.notNull(enchantment, "The enchantment must not be null");
        if (level <= 0) {
            throw new IllegalArgumentException("Level must be at least 1.");
        }
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Adds multiple enchantments to the item.
     *
     * @param enchantments What enchantment types should the item get.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IllegalArgumentException If the enchantments are inappropirate.
     */
    public ItemBuilder enchants(Enchantment... enchantments) throws IllegalArgumentException {
        Validate.notNull(enchantments, "The enchantments must not be null");
        for (Enchantment enchantment : enchantments) {
            this.enchantments.put(enchantment, 1);
        }
        return this;
    }


    /**
     * Adds multiple enchantments to the item.
     *
     * @param enchantments What enchantment types should the item get.
     * @param level        What level should the enchantments be.
     * @return The new ItemBuilder object with which further changes can be made.
     * @throws IndexOutOfBoundsException If the lists are not the same size.
     * @throws IllegalArgumentException  If one of the passed values is inappropriate.
     */
    public ItemBuilder enchants(List<Enchantment> enchantments, List<Integer> level) throws IndexOutOfBoundsException, IllegalArgumentException {
        Validate.notNull(enchantments, "The enchantment must not be null");
        Validate.notNull(level, "The level must not be null");
        if (enchantments.size() != level.size()) {
            throw new IndexOutOfBoundsException("The passed parameters must have the same size.");
        }

        for (int i = 0; i < enchantments.size(); i++) {
            Enchantment enchantment = enchantments.get(i);
            int enchantmentLevel = level.get(i);

            this.enchantments.put(enchantment, enchantmentLevel);
        }
        return this;
    }

    public ItemStack build() {
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.material, this.amount);
        }
        if (this.itemMeta == null) {
            this.itemMeta = this.itemStack.getItemMeta();
        }
        if (this.displayName != null) {
            this.itemMeta.setDisplayName(this.displayName);
        }
        if (!this.lore.isEmpty()) {
            this.itemMeta.setLore(this.lore);
        }
        if (!this.flags.isEmpty()) {
            this.flags.forEach(itemFlag -> this.itemMeta.addItemFlags(itemFlag));
        }
        if (!this.enchantments.isEmpty()) {
            this.enchantments.forEach((enchantment, level) -> this.itemMeta.addEnchant(enchantment, level, true));
        }

        this.itemStack.setAmount(this.amount);

        if (this.glow) {
            this.itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);

            if (!this.itemMeta.getItemFlags().isEmpty())
                this.itemMeta.getItemFlags().clear();
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        this.itemStack.setItemMeta(this.itemMeta);

        return this.itemStack;
    }

}
