package dev.openfeature.sdk.e2e.tracking;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StepDefinitions {

    private static Client client;
    private static FeatureProvider featureProvider;
    private boolean hasThrown;


    @SneakyThrows
    @BeforeAll()
    @Given("a provider is registered")
    public static void setup() {
        featureProvider = mock(FeatureProvider.class);
        OpenFeatureAPI.getInstance().setProviderAndWait(featureProvider);
        client = OpenFeatureAPI.getInstance().getClient();
    }


    @When("an event was tracked with tracking event name {string}")
    public void an_event_was_tracked_with_tracking_event_name(String eventName) {
        if ("NULL".equals(eventName)) {
            eventName = null;
        }
        try {
            client.track(eventName);
        } catch (IllegalArgumentException | NullPointerException e) {
            hasThrown = true;
        }
    }


    @Then("nothing should have been tracked")
    public void nothing_should_have_been_tracked() {
        verify(featureProvider, never()).track(any(), any(), any());
    }

    @And("the tracking operation shall error")
    public void theTrackingOperationShallError() {
        assertTrue(hasThrown);
    }

    @Then("the tracking provider should have been called with event name {string}")
    public void the_tracking_provider_should_have_been_called_with_event_name(String eventName) {
        verify(featureProvider, times(1)).track(eq(eventName), any(), any());
    }
}
