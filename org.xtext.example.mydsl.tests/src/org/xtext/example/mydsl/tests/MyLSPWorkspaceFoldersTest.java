package org.xtext.example.mydsl.tests;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.xtext.ide.server.ILanguageServerAccess;
import org.eclipse.xtext.ide.server.WorkspaceManager;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.testing.AbstractLanguageServerTest;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class MyLSPWorkspaceFoldersTest extends AbstractLanguageServerTest {
	public MyLSPWorkspaceFoldersTest() {
		super("rd1");
	}

	@Inject
	private WorkspaceManager workspaceManager;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testInitialize1() throws Exception {
		// This fails
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("c:/Users/jnt0r/IdeaProjects/blub", "root1")));
		});
	}
	@Test
	public void testInitialize2() throws Exception {
		// This fails
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("c:/Users/jnt0r/IdeaProjects/blub")));
		});
	}
	
	@Test
	public void testInitialize3() throws Exception {
		// This fails
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("file:///c%3A/Users/jnt0r/IdeaProjects/blub")));
		});
	}
	
	@Test
	public void testInitialize4() throws Exception {
		// This succeeds
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("file:///c%3A/Users/jnt0r/IdeaProjects/blub", "root1")));
		});
	}
	
	@Test
	public void testInitialize5() throws Exception {
		// This fails
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("file:///c:/Users/jnt0r/IdeaProjects/blub")));
		});
	}
	
	@Test
	public void testInitialize6() throws Exception {
		// This succeeds
		initialize((InitializeParams it) -> {
			it.setWorkspaceFolders(Lists.newArrayList(new WorkspaceFolder("file:///c:/Users/jnt0r/IdeaProjects/blub", "root1")));
		});
	}
	
	@Test
	public void testInitialize7() throws Exception {
		// This succeeds
		initialize((InitializeParams it) -> {
			it.setRootUri("file:///c:/Users/jnt0r/IdeaProjects/blub");
		});
		Assert.assertEquals(0, getDiagnostics().size());
	}

	protected void withBuild(Procedure0 lambda) throws Exception {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		workspaceManager.addBuildListener(new ILanguageServerAccess.IBuildListener() {
			@Override
			public void afterBuild(List<IResourceDescription.Delta> it) {
				workspaceManager.removeBuildListener(this);
				future.complete(null);
			}
		});
		lambda.apply();
		future.get();
	}

}

