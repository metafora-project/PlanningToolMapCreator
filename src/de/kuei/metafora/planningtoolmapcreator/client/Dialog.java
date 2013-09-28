package de.kuei.metafora.planningtoolmapcreator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.kuei.metafora.planningtoolmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.planningtoolmapcreator.client.serverlink.MapInitAsync;
import de.kuei.metafora.planningtoolmapcreator.client.util.InputFilter;

public class Dialog implements ClickHandler, KeyPressHandler {

	final static Languages language = GWT.create(Languages.class);
	private static final MapInitAsync mapInit = GWT.create(MapInit.class);

	private static Dialog dialog = null;

	public static Dialog getInstance() {
		if (dialog == null) {
			dialog = new Dialog();
		}
		return dialog;
	}

	private VerticalPanel rootPanel;
	private SuggestBox box = null;
	private Button btnCreate = null;

	public Dialog() {
	}

	public void main(MultiWordSuggestOracle oracle) {
		rootPanel = new VerticalPanel();
		rootPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		rootPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		rootPanel.setWidth("100%");
		rootPanel.getElement().getStyle().setBackgroundColor("#eaeaea");
		RootPanel.get().add(rootPanel);

		Label label = new Label(language.EnterMapname());
		label.getElement().getStyle().setMargin(10, Unit.PX);
		label.setHeight("10px");
		rootPanel.add(label);

		HorizontalPanel userrow = new HorizontalPanel();

		CaptionPanel cpan = new CaptionPanel(language.Mapname());
		cpan.getElement().getStyle().setMargin(10, Unit.PX);
		cpan.setHeight("50px");
		box = new SuggestBox(oracle);
		box.addKeyPressHandler(this);
		cpan.add(box);
		userrow.add(cpan);
		rootPanel.add(userrow);

		FlowPanel row = new FlowPanel();
		btnCreate = new Button(language.CreateMap());
		btnCreate.addClickHandler(this);
		btnCreate.getElement().getStyle().setMargin(10, Unit.PX);
		btnCreate.getElement().getStyle().setMarginRight(100, Unit.PX);
		row.add(btnCreate);
		rootPanel.add(row);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(btnCreate)) {
			createMap();
		}

	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (((int) event.getCharCode()) == 13
				|| (((int) event.getCharCode()) == 0 && ((int) event
						.getNativeEvent().getKeyCode()) == 13)) {
			if (event.getSource().equals(box)) {
				createMap();
			}
		}
	}

	private void createMap() {
		String input = box.getText();

		final String mapname = InputFilter.filterString(input);

		CreatingMap.getInstance().main();
		hideUi();

		// send create map command to server
		mapInit.sendCreateMapCommand(PlanningToolMapCreator.token,
				PlanningToolMapCreator.users,
				PlanningToolMapCreator.md5Password,
				PlanningToolMapCreator.groupId,
				PlanningToolMapCreator.challengeId,
				PlanningToolMapCreator.challengeName,
				PlanningToolMapCreator.ptNodeId, PlanningToolMapCreator.ptMap,
				mapname, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {

					}

					@Override
					public void onFailure(Throwable caught) {
						CreatingMap.getInstance().label.setText(language
								.CouldNotBeCreated());
					}
				});
	}

	/**
	 * Hides the graphical user interface.
	 */
	public void hideUi() {
		rootPanel.setVisible(false);
	}

	/**
	 * Shows the graphical user interface.
	 */
	public void showUi() {
		rootPanel.setVisible(true);
	}
}
