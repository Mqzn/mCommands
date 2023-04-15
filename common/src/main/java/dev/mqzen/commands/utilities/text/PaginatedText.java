package net.versemc.api.utilities.text;

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class PaginatedText<T extends TextConvertible> {

	public final static int DEFAULT_ITEMS_PER_PAGE = 10;
	private final @NonNull Set<TextDecoration> headerDecorations = new HashSet<>();
	private final @Getter int itemsPerPage;
	private final List<T> textObjects = new ArrayList<>();
	private final @NonNull Map<Integer, TextPage<T>> pages = new HashMap<>();
	private @NonNull TextColor primary_color = NamedTextColor.GOLD,
					secondary_color = NamedTextColor.YELLOW,
					headerLineColor = NamedTextColor.DARK_GRAY;
	private TextComponent headerLine = Component.text("===========");
	private @Nullable TextComponent title;
	private @Nullable ItemPageTextDisplayer<T> displayer;

	private PaginatedText(int itemsPerPage) {
		this(null, itemsPerPage);
	}

	private PaginatedText() {
		this(DEFAULT_ITEMS_PER_PAGE);
	}

	private PaginatedText(@Nullable TextComponent title, int itemsPerPage) {
		this.title = title;
		this.itemsPerPage = itemsPerPage;
	}

	public static <T extends TextConvertible> PaginatedText<T> create() {
		return new PaginatedText<>();
	}

	public static <T extends TextConvertible> PaginatedText<T> create(TextComponent title, int itemsPerPage) {
		return new PaginatedText<>(title, itemsPerPage);
	}

	public static <T extends TextConvertible> PaginatedText<T> create(int itemsPerPage) {
		return new PaginatedText<>(itemsPerPage);
	}

	public void add(T object) {
		textObjects.add(object);
	}

	public PaginatedText<T> remove(T object) {
		textObjects.remove(object);
		return this;
	}

	public PaginatedText<T> withPrimaryColor(TextColor primary) {
		this.primary_color = primary;
		return this;
	}

	public PaginatedText<T> withSecondaryColor(TextColor secondary) {
		this.secondary_color = secondary;
		return this;
	}

	public PaginatedText<T> withHeaderLineColor(TextColor headerLineColor) {
		this.headerLineColor = headerLineColor;
		return this;
	}

	public PaginatedText<T> addHeaderLineDecoration(TextDecoration decoration) {
		this.headerDecorations.add(decoration);
		return this;
	}

	public PaginatedText<T> withTitle(@Nullable TextComponent title) {
		this.title = title;
		return this;
	}

	public PaginatedText<T> withDisplayer(@NonNull ItemPageTextDisplayer<T> displayer) {
		this.displayer = displayer;
		return this;
	}

	public void paginate() {

		title = title == null ? Component.text("Menu Page", primary_color) : title.append(Component.text(" Menu Page", title.color()));
		headerLine = headerLine.color(headerLineColor);
		for (TextDecoration decoration : headerDecorations) {
			headerLine = headerLine.decorate(decoration);
		}

		for (int i = 1; i <= textObjects.size(); i++) {
			T obj = textObjects.get(i - 1);
			//calculate the page from it's index and the items per page
			int page = (int) Math.ceil((double) (i) / (itemsPerPage));

			pages.compute(page, (index, existingPage) -> {
				if (existingPage == null) {
					List<T> list = new ArrayList<>(itemsPerPage);
					list.add(obj);
					return TextPage.of(page, itemsPerPage, list);
				}

				existingPage.add(obj);
				return existingPage;
			});

		}

	}

	public @Nullable TextPage<T> getPage(int index) {
		return pages.get(index);
	}

	public int maxPages() {
		return pages.size();
	}

	public void displayPage(@NonNull CommandSender sender, int page) {

		int maxPages = pages.size();
		if (page > maxPages || page < 1) {
			throw new IllegalArgumentException("Page must be in range 1-" + maxPages);
		}

		if (title == null || headerLine == null || displayer == null) {
			throw new IllegalStateException("The text menu is not fully ready yet (early access)!!");
		}

		TextPage<T> textPage = getPage(page);
		if (textPage == null) return;

		TextComponent firstLine = Component.empty().append(headerLine)
						.append(Component.space()).append(Component.space().decorations(new HashMap<>()))
						.append(Component.text(title.content(), title.color()).decorate(TextDecoration.BOLD))
						.append(Component.space()).append(Component.space())
						.append(Component.text("(", NamedTextColor.GRAY))
						.append(Component.text(page, secondary_color))
						.append(Component.text("/", primary_color))
						.append(Component.text(maxPages, secondary_color))
						.append(Component.text(")", NamedTextColor.GRAY))
						.append(Component.text(" ", NamedTextColor.WHITE))
						.append(headerLine);

		sender.sendMessage(firstLine);
		displayer.display(sender, textPage);
	}


}
