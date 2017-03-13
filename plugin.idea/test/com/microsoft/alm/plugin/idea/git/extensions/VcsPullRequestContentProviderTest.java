// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.plugin.idea.git.extensions;

import com.intellij.openapi.project.Project;
import com.microsoft.alm.plugin.idea.IdeaAbstractTest;
import com.microsoft.alm.plugin.idea.common.utils.IdeaHelper;
import com.microsoft.alm.plugin.idea.common.utils.VcsHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({VcsHelper.class, IdeaHelper.class})
public class VcsPullRequestContentProviderTest extends IdeaAbstractTest {
    private VcsPullRequestContentProvider.VcsPullRequestVisibilityPredicate pullRequestVisibilityPredicate;

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(VcsHelper.class, IdeaHelper.class);
        pullRequestVisibilityPredicate = new VcsPullRequestContentProvider.VcsPullRequestVisibilityPredicate();
    }

    @Test
    public void testTabEnabled_NotGit() {
        when(VcsHelper.isGitVcs(mockProject)).thenReturn(false);
        assertFalse(pullRequestVisibilityPredicate.fun(mockProject));
    }

    @Test
    public void testTabEnabled_GitNotRider() {
        when(VcsHelper.isGitVcs(mockProject)).thenReturn(true);
        when(IdeaHelper.isRider()).thenReturn(false);
        assertTrue(pullRequestVisibilityPredicate.fun(mockProject));
    }

    @Test
    public void testTabEnabled_GitRiderVsts() {
        when(VcsHelper.isGitVcs(mockProject)).thenReturn(true);
        when(IdeaHelper.isRider()).thenReturn(true);
        when(VcsHelper.isVstsRepo(mockProject)).thenReturn(true);
        assertTrue(pullRequestVisibilityPredicate.fun(mockProject));
    }

    @Test
    public void testTabEnabled_GitRiderNotVsts() {
        when(VcsHelper.isGitVcs(mockProject)).thenReturn(true);
        when(IdeaHelper.isRider()).thenReturn(true);
        when(VcsHelper.isVstsRepo(mockProject)).thenReturn(false);
        assertFalse(pullRequestVisibilityPredicate.fun(mockProject));
    }
}
