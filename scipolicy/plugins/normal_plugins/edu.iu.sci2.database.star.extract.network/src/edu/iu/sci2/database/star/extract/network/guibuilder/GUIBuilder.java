package edu.iu.sci2.database.star.extract.network.guibuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utility.datastructure.ObjectContainer;
import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.field.FieldValidationAction;
import org.cishell.utility.swt.GUIBuilderUtilities;
import org.cishell.utility.swt.GUICanceledException;
import org.cishell.utility.swt.model.SWTModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import edu.iu.sci2.database.star.extract.network.StarDatabaseDescriptor;
import edu.iu.sci2.database.star.extract.network.guibuilder.attribute.AttributeListWidget;

public abstract class GUIBuilder {
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 850;

	public static final int INSTRUCTIONS_WIDTH = 400;

	public static final int AGGREGATE_LIST_HEIGHT = 300;

	public static final int DEFAULT_AGGREGATE_WIDGET_COUNT = 1;

	public static final String INSTRUCTIONS_GROUP_TEXT = "";
	public static final String HEADER_GROUP_TEXT = "";
	public static final String NODE_ATTRIBUTES_GROUP_TEXT = "Node Attributes";
	public static final String EDGE_ATTRIBUTES_GROUP_TEXT = "Edge Attributes";

	public static final String FINISHED_BUTTON_TEXT = "Run the Extraction!";

	public static final String NODE_TYPE = "Node";
	public static final String EDGE_TYPE = "Edge";

	public static final String HEADER_GROUP_NAME = "header";

	public static final String NODE_ATTRIBUTE_FUNCTION_GROUP_NAME = "nodeAttributeFunction";
	public static final String NODE_CORE_ENTITY_COLUMN_GROUP_NAME = "nodeCoreEntityColumn";
	public static final String NODE_RESULT_NAME_GROUP_NAME = "nodeResult";

	public static final String EDGE_ATTRIBUTE_FUNCTION_GROUP_NAME = "edgeAttributeFunction";
	public static final String EDGE_CORE_ENTITY_COLUMN_GROUP_NAME = "edgeCoreEntityColumn";
	public static final String EDGE_RESULT_NAME_GROUP_NAME = "edgeResult";

	protected Button finishedButton;
	protected DisableFinishedButtonAction disableFinishedButtonAction =
		new DisableFinishedButtonAction();

	public abstract DataModel createGUI(
			String windowTitle,
			int windowWidth,
			int windowHeight,
			StarDatabaseDescriptor databaseDescriptor)
			throws GUICanceledException, UniqueNameException;

	public static void runGUI(Display display, Shell shell, int windowHeight) {
		GUIBuilderUtilities.openShell(shell, windowHeight, true);
    	GUIBuilderUtilities.swtLoop(display, shell);
	}

	protected static Composite createInstructionsArea(Composite parent) {
		Composite instructionsArea = new Composite(parent, SWT.NONE);
		instructionsArea.setLayoutData(createInstructionsAreaLayoutData());
		instructionsArea.setLayout(createInstructionsAreaLayout());
		instructionsArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		return instructionsArea;
	}

	private static GridData createInstructionsAreaLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = INSTRUCTIONS_WIDTH;

		return layoutData;
	}

	private static GridLayout createInstructionsAreaLayout() {
		GridLayout layout = new GridLayout(2, false);

		return layout;
	}

	protected static Group createHeaderGroup(Composite parent) {
		Group headerGroup = new Group(parent, SWT.NONE);
		headerGroup.setLayoutData(createHeaderGroupLayoutData());
		headerGroup.setLayout(createHeaderGroupLayout());
		headerGroup.setText(HEADER_GROUP_TEXT);

		return headerGroup;
	}

	private static GridData createHeaderGroupLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = INSTRUCTIONS_WIDTH;

		return layoutData;
	}

	private static GridLayout createHeaderGroupLayout() {
		GridLayout layout = new GridLayout(2, false);

		return layout;
	}

	protected static Group createAggregatesGroup(Composite parent, String groupText) {
		Group nodeAggregatesGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		nodeAggregatesGroup.setLayoutData(createAggregatesGroupLayoutData());
		nodeAggregatesGroup.setLayout(createAggregatesGroupLayout());
		nodeAggregatesGroup.setText(groupText);

		return nodeAggregatesGroup;
	}

	private static GridData createAggregatesGroupLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = 2;

		return layoutData;
	}

	private static GridLayout createAggregatesGroupLayout() {
		GridLayout layout = new GridLayout(1, false);

		return layout;
	}

	protected static Group createFooterGroup(Composite parent) {
		Group footerGroup = new Group(parent, SWT.NONE);
		footerGroup.setLayoutData(createFooterGroupLayoutData());
		footerGroup.setLayout(createFooterGroupLayout());
		footerGroup.setText(HEADER_GROUP_TEXT);

		return footerGroup;
	}

	private static GridData createFooterGroupLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.horizontalSpan = 2;

		return layoutData;
	}

	private static GridLayout createFooterGroupLayout() {
		GridLayout layout = new GridLayout(2, false);

		return layout;
	}

	protected AttributeListWidget createAggregateWidget(
			SWTModel model,
			String aggregateFunctionGroupName,
			String coreEntityColumnGroupName,
			Collection<String> coreEntityColumnLabels,
			Map<String, String> coreEntityColumnsByLabels,
			String resultColumnLabelGroupName,
			String type,
			Composite parent) {
		AttributeListWidget aggregateList = new AttributeListWidget(
			model,
			aggregateFunctionGroupName,
			coreEntityColumnGroupName,
			coreEntityColumnLabels,
			coreEntityColumnsByLabels,
			resultColumnLabelGroupName,
			type,
			parent,
			this.disableFinishedButtonAction);
		aggregateList.setLayoutData(createAggregateListLayoutData());

		return aggregateList;
	}

	protected static GridData createAggregateListLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.heightHint = AGGREGATE_LIST_HEIGHT;

		return layoutData;
	}

	protected static Button createFinishedButton(
			final Composite parent,
			int horizontalSpan,
			final ObjectContainer<Boolean> userFinished) {
		Button finishedButton = new Button(parent, SWT.PUSH);
		finishedButton.setLayoutData(createFinishedButtonLayoutData(horizontalSpan));
		finishedButton.setText(FINISHED_BUTTON_TEXT);
		finishedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				parent.getShell().close();
				userFinished.object = true;
			}
		});

		return finishedButton;
	}

	private static GridData createFinishedButtonLayoutData(int horizontalSpan) {
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.horizontalSpan = horizontalSpan;

		return layoutData;
	}

	public class DisableFinishedButtonAction implements FieldValidationAction<String> {
		private Map<DataModelField<String>, Boolean> fieldsToValidationStatuses =
			new HashMap<DataModelField<String>, Boolean>();

		public synchronized void fieldDoesValidate(DataModelField<String> field) {
			this.fieldsToValidationStatuses.put(field, true);
			System.err.println("----");
			for (DataModelField<String> key : this.fieldsToValidationStatuses.keySet()) {
				System.err.println(key.getName() + " field validates? " + this.fieldsToValidationStatuses.get(key) + " " + key.getValue());
			}

			GUIBuilder.this.finishedButton.setEnabled(
				!this.fieldsToValidationStatuses.containsValue(false));
		}

		public synchronized void fieldDoesNotValidate(
				DataModelField<String> field, Collection<ModelValidationException> reasons) {
			this.fieldsToValidationStatuses.put(field, false);
			System.err.println("----");
			for (DataModelField<String> key : this.fieldsToValidationStatuses.keySet()) {
				System.err.println(key.getName() + " field validates? " + this.fieldsToValidationStatuses.get(key) + " " + key.toString());
			}

			GUIBuilder.this.finishedButton.setEnabled(false);
		}

		public void fieldDisposed(DataModelField<String> field) {
			this.fieldsToValidationStatuses.remove(field);
			GUIBuilder.this.finishedButton.setEnabled(
				!this.fieldsToValidationStatuses.containsValue(false));
		}

		protected void addField(DataModelField<String> field, boolean validationStatus) {
			this.fieldsToValidationStatuses.put(field, validationStatus);
		}
	}
}