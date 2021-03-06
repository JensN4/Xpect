package org.xpect.xtext.lib.setup.workspace;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.xpect.setup.XpectSetup;
import org.xpect.xtext.lib.setup.FileSetupContext;

@XpectSetup(WorkspaceDefaultsSetup.class)
public class Workspace extends Container<IWorkspaceRoot> {

	public static class Instance {
		private IFile thisFile;
		private IProject thisProject;
		private IWorkspace workspace;

		public IFile getThisFile() {
			return thisFile;
		}

		public IProject getThisProject() {
			return thisProject;
		}

		public IWorkspace getWorkspace() {
			return workspace;
		}

		public void setThisFile(IFile thisFile) {
			this.thisFile = thisFile;
		}

		public void setThisProject(IProject thisProject) {
			this.thisProject = thisProject;
		}

		public void setWorkspace(IWorkspace workspace) {
			this.workspace = workspace;
		}
	}

	public Workspace() {
	}

	public void cleanWorkspace() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = projects.length - 1; i >= 0; i--)
			try {
				projects[i].delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
	}

	public Workspace.Instance configureWorkspace(final FileSetupContext ctx) {
		final Instance instance = new Instance();
		try {
			new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					try {
						configure(ctx, root);
						createMembers(ctx, root, instance);
					} catch (IOException e) {
						throw new CoreException(new Status(IStatus.ERROR, "org.xpect.xtext.lib", "Error initializing test workspace", e));
					}
				}
			}.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	protected <T> T findRecursive(Container<?> container, Class<T> type) {
		for (IResourceFactory<?, ?> child : container.getMemberFactories()) {
			if (type.isInstance(child))
				return (T) child;
			if (child instanceof Container) {
				T result = findRecursive((Container<?>) child, type);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	public Project getDefaultProject() {
		for (IResourceFactory<?, ?> fact : getMemberFactories())
			if (fact instanceof Project)
				return (Project) fact;
		return null;
	}

	public org.xpect.xtext.lib.setup.workspace.ThisFile getThisFile() {
		return findRecursive(this, org.xpect.xtext.lib.setup.workspace.ThisFile.class);
	}

	public void waitForAutoBuild() {
		boolean interrupted = false;
		do {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				interrupted = false;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				interrupted = true;
			}
		} while (interrupted);
	}
}
