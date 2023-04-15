package dev.mqzen.commands.utilities.text;

import dev.mqzen.commands.help.CommandHelpProvider;
import dev.mqzen.commands.sender.SenderWrapper;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PaginatedText<S, T extends TextConvertible<S>> {

	public final static int DEFAULT_ITEMS_PER_PAGE = 10;

	@NotNull
	private final SenderWrapper<S> wrapper;

	@Getter
	private final int itemsPerPage;

	@NotNull
	private final List<T> textObjects = new ArrayList<>();

	@NotNull
	private final Map<Integer, TextPage<S, T>> pages = new HashMap<>();

	@NotNull
	private final CommandHelpProvider provider;

	@NotNull
	private TextComponent headerLine = Component.text("===========");

	@Nullable
	private ItemPageTextDisplayer<S, T> displayer;

	@NotNull
	private NamedTextColor primaryColor = NamedTextColor.YELLOW, secondaryColor = NamedTextColor.GOLD;

	private PaginatedText(@NotNull CommandHelpProvider provider,
	                      @NotNull SenderWrapper<S> wrapper) {
		this(provider, wrapper, DEFAULT_ITEMS_PER_PAGE);
	}

	private PaginatedText(@NotNull CommandHelpProvider provider,
	                      @NotNull SenderWrapper<S> wrapper,
	                      int itemsPerPage) {
		this.provider = provider;
		this.wrapper = wrapper;
		this.itemsPerPage = itemsPerPage;
	}

	public static <S, T extends TextConvertible<S>> PaginatedText<S, T> create(@NotNull CommandHelpProvider provider,
	                                                                           @NotNull SenderWrapper<S> wrapper) {
		return new PaginatedText<>(provider, wrapper);
	}

	public static <S, T extends TextConvertible<S>> PaginatedText<S, T> create(@NotNull CommandHelpProvider provider,
	                                                                           @NotNull SenderWrapper<S> wrapper,
	                                                                           int itemsPerPage) {
		return new PaginatedText<>(provider, wrapper, itemsPerPage);
	}


	public void add(T object) {
		textObjects.add(object);
	}

	public PaginatedText<S, T> remove(@NotNull T object) {
		textObjects.remove(object);
		return this;
	}

	public PaginatedText<S, T> withHeaderLine(@NotNull String line) {
		this.headerLine = Component.text(line);
		return this;
	}


	public PaginatedText<S, T> withPrimaryColor(@NotNull NamedTextColor primaryColor) {
		this.primaryColor = primaryColor;
		return this;
	}

	public PaginatedText<S, T> withSecondaryColor(@NotNull NamedTextColor secondaryColor) {
		this.secondaryColor = secondaryColor;
		return this;
	}

	public PaginatedText<S, T> withDisplayer(@NonNull ItemPageTextDisplayer<S, T> displayer) {
		this.displayer = displayer;
		return this;
	}

	public void paginate() {

		for (int i = 1; i <= textObjects.size(); i++) {
			T obj = textObjects.get(i - 1);
			//calculate the page from it's index and the items per page
			int page = (int) Math.ceil((double) (i) / (itemsPerPage));

			pages.compute(page, (index, existingPage) -> {
				if (existingPage == null) {
					List<T> list = new ArrayList<>(itemsPerPage);
					list.add(obj);
					return new TextPage<>(page, itemsPerPage, list);
				}

				existingPage.add(obj);
				return existingPage;
			});

		}

	}

	public @Nullable TextPage<S, T> getPage(int index) {
		return pages.get(index);
	}

	public int maxPages() {
		return pages.size();
	}

	public void displayPage(@NotNull String label, @NonNull S sender, int page) {

		int maxPages = pages.size();
		if (page > maxPages || page < 1) {
			throw new IllegalArgumentException("Page must be in range 1-" + maxPages);
		}

		if (displayer == null) {
			throw new IllegalStateException("The text menu is not fully ready yet (early access)!!");
		}

		TextPage<S, T> textPage = getPage(page);
		if (textPage == null) return;


		TextComponent line = headerLine.style(provider.lineStyle());
		TextComponent firstLine = Component.empty().append(line)
						.append(Component.space()).append(Component.space().decorations(new HashMap<>()))
						.append(provider.header(label))
						.append(Component.space()).append(Component.space())
						.append(Component.text("(", secondaryColor))
						.append(Component.text(page, primaryColor))
						.append(Component.text("/", secondaryColor))
						.append(Component.text(maxPages, primaryColor))
						.append(Component.text(")", secondaryColor))
						.append(Component.text(" ", NamedTextColor.WHITE))
						.append(line);

		wrapper.sendMessage(sender, firstLine);
		displayer.display(wrapper, sender, textPage);
	}


}
