package net.veldor.rdc_informer.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.util.Objects;

public class HaveExecutionCell<S, T> extends TableCell<S, T> {
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
        return list -> new HaveExecutionCell<>(converter);
    }

    protected static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem()
                .toString() : converter.toString(cell.getItem());
    }

    protected void updateItem(final Cell<T> cell, final StringConverter<T> converter) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setTooltip(null);
            cell.setStyle("");

        } else {
            String text = getItemText(cell, converter);
            cell.setText(text);
            if (!Objects.equals(text, "Да")) {
                cell.setTextFill(Color.WHITE);
                cell.setStyle("-fx-background-color: rgba(246,172,172,0.62);-fx-font-weight: bold;");
            } else {
                cell.setTextFill(Color.BLACK);
                cell.setStyle("");
            }
            cell.setFocusTraversable(true);

            Tooltip tooltip = new Tooltip(Objects.equals(text, "Да") ? "Архив обследования загружен" : "Архив обследования ещё не загружен");
            tooltip.setWrapText(true);
            tooltip.prefWidthProperty().bind(cell.widthProperty());
            cell.setTooltip(tooltip);
        }
    }

    private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");

    /**
     * The easiest way to get this working is to call this class's static forTableColumn() method:
     * <code>
     * someColumn.setCellFactory(TooltippedTableCell.forTableColumn());
     * </code>
     */
    public HaveExecutionCell() {
        this(null);
    }

    public HaveExecutionCell(StringConverter<T> converter) {
        this.getStyleClass().add("tooltipped-table-cell");
        setConverter(converter);
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }


    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter());
    }
}