package at.quelltextlich.phabricator.conduit.raw;

import com.google.gson.JsonNull;
import org.easymock.Capture;

import java.util.Arrays;
import java.util.Map;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

public class DifferentialModuleTest extends ModuleTestCase {

  public void testGetRawDiff() throws Exception {
    final JsonNull ret = JsonNull.INSTANCE;

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("differential.getrawdiff"), capture(paramsCapture)))
            .andReturn(ret).once();

    replayMocks();

    final DifferentialModule module = getModule();
    DifferentialModule.GetRawDiffResult rawDiffResult = module.getRawDiff(514);
    assertNotNull(rawDiffResult.getResult());
  }

  public void testQueryDiff() throws Exception {
    final JsonNull ret = JsonNull.INSTANCE;

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("differential.query"), capture(paramsCapture)))
            .andReturn(ret).once();

    replayMocks();

    final DifferentialModule module = getModule();
    DifferentialModule.DifferentialResult rawDiffResult = module.query(
            null, null, null, null, null, null, null, null, null, Arrays.asList(504).iterator(), null, null, null, null);
    assertTrue(rawDiffResult.size() > 0);
  }

  @Override
  protected DifferentialModule getModule() {
    return new DifferentialModule(connection, sessionHandler);
  }
}
