package com.scdeco.embdesign;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewerWrapper {
	  static String[] COLUMN_NAMES = new String[] { "FOO", "BAR" };
	    static int[] COLUMN_WIDTHS = new int[] { 300, 200 };
	    static String[] COLUMNS_PROPERTIES = new String[] { "foo_prop", "bar_prop" };

	    static class Model {
	        private String foo;
	        private String bar;

	        public Model(String foo, String bar) {
	            super();
	            this.foo = foo;
	            this.bar = bar;
	        }

	        public String getFoo() {
	            return foo;
	        }

	        public void setFoo(String foo) {
	            this.foo = foo;
	        }

	        public String getBar() {
	            return bar;
	        }

	        public void setBar(String bar) {
	            this.bar = bar;
	        }
	    }

	    static class ModelContentProvider implements IStructuredContentProvider {

	        @Override
	        public Object[] getElements(Object inputElement) {
	            // The inputElement comes from view.setInput()
	            if (inputElement instanceof List) {
	                @SuppressWarnings("rawtypes")
					List models = (List) inputElement;
	                return models.toArray();
	            }
	            return new Object[0];
	        }

	        @Override
	        public void dispose() {
	        }

	        @Override
	        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	        }

	    }

	    static class ModelLabelProvider extends LabelProvider implements
	            ITableLabelProvider {

	        @Override
	        public Image getColumnImage(Object element, int columnIndex) {
	            // no image to show
	            return null;
	        }

	        @Override
	        public String getColumnText(Object element, int columnIndex) {
	            // each element comes from the ContentProvider.getElements(Object)
	            if (!(element instanceof Model)) {
	                return "";
	            }
	            Model model = (Model) element;
	            switch (columnIndex) {
	            case 0:
	                return model.getFoo();
	            case 1:
	                return model.getBar();
	            default:
	                break;
	            }
	            return "";
	        }
	    }

	    static class ModelCellModifier implements ICellModifier {
	        TableViewer viewer;

	        public ModelCellModifier(TableViewer viewer) {
	            this.viewer = viewer;
	        }

	        @Override
	        public boolean canModify(Object element, String property) {
	            // property is defined by viewer.setColumnProperties()
	            // allow the FOO column can be modified.
	        	
	        	return "foo_prop".equals(property)|"bar_prop".equals(property);
	        }

	        @Override
	        public Object getValue(Object element, String property) {
	            if ("foo_prop".equals(property)) {
	                return ((Model) element).getFoo();
	            }
	            if ("bar_prop".equals(property)) {
	                return ((Model) element).getBar();
	            }
	            return "";
	        }

	        @Override
	        public void modify(Object element, String property, Object value) {
	            if ("foo_prop".equals(property)) {
	                TableItem item = (TableItem) element;
	                ((Model) item.getData()).setFoo("" + value);
	                // refresh the viewer to show the changes to our user.
	                viewer.refresh();
	            }
	            if ("bar_prop".equals(property)) {
	                TableItem item = (TableItem) element;
	                ((Model) item.getData()).setBar("" + value);
	                // refresh the viewer to show the changes to our user.
	                viewer.refresh();
	            }
	        }
	    }

	    public static void main(String[] args) {
	        final List<Model> models = new ArrayList<Model>();
	        models.add(new Model("a", "b"));
	        models.add(new Model("x", "y"));

	        final ApplicationWindow app = new ApplicationWindow(null) {
	            @Override
	            protected Control createContents(Composite parent) {
	                TableViewer tableViewer = new TableViewer(parent);
	                Table table = tableViewer.getTable();
	                table.setHeaderVisible(true);
	                table.setLinesVisible(true);

	                for (int i = 0; i < COLUMN_NAMES.length; i++) {
	                    TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
	                    tableColumn.setText(COLUMN_NAMES[i]);
	                    tableColumn.setWidth(COLUMN_WIDTHS[i]);
	                }

	                tableViewer.setContentProvider(new ModelContentProvider());
	                tableViewer.setLabelProvider(new ModelLabelProvider());

	                tableViewer.setColumnProperties(COLUMNS_PROPERTIES);
	                tableViewer.setCellEditors(new CellEditor[] {
	                        new TextCellEditor(table), new TextCellEditor(table) });
	                tableViewer.setCellModifier(new ModelCellModifier(tableViewer));

	                tableViewer.setInput(models);
	                return parent;
	            }
	        };

	        Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
	                new Runnable() {
	                    @Override
	                    public void run() {
	                        app.setBlockOnOpen(true);
	                        app.open();
	                        Display.getDefault().dispose();
	                    }
	                });

	    }
	
	

		
	
}