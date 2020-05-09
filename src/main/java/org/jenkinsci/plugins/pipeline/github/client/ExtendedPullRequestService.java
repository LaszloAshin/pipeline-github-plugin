package org.jenkinsci.plugins.pipeline.github.client;

import com.google.gson.reflect.TypeToken;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Aaron Whiteside
 */
public class ExtendedPullRequestService extends PullRequestService {

    public ExtendedPullRequestService(final ExtendedGitHubClient client) {
        super(client);
    }

    @Override
    public ExtendedGitHubClient getClient() {
        return (ExtendedGitHubClient) super.getClient();
    }

    public ExtendedPullRequest editPullRequest(final IRepositoryIdProvider repository, final ExtendedPullRequest request) {
        Objects.requireNonNull(request, "request cannot be null");

        String id = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(id);
        uri.append("/pulls");
        uri.append('/').append(request.getNumber());
        Map<String, Object> params = editPrMap(request);
        return (ExtendedPullRequest) getClient().patch(uri.toString(), params, ExtendedPullRequest.class);
    }

    protected Map<String, Object> editPrMap(final ExtendedPullRequest request) {
        Map<String, Object> params = new HashMap<>();
        if (request.getTitle() != null) {
            params.put("title", request.getTitle());
        }

        if (request.getBody() != null) {
            params.put("body", request.getBody());
        }

        if (request.getState() != null) {
            params.put("state", request.getState());
        }

        if (request.getBase() != null) {
            params.put("base", request.getBase().getRef());
        }

        if (request.isMaintainerCanModify() != null) {
            params.put("maintainer_can_modify", request.isMaintainerCanModify());
        }

        return params;
    }


    @Override
    public ExtendedPullRequest getPullRequest(final IRepositoryIdProvider repository, final int id) {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        GitHubRequest request = this.createRequest();
        request.setUri(uri);
        request.setType(ExtendedPullRequest.class);
        return (ExtendedPullRequest) getClient().getUnchecked(request).getBody();
    }

    public ExtendedMergeStatus merge(final IRepositoryIdProvider repository,
                                     final int id,
                                     final String commitTitle,
                                     final String commitMessage,
                                     final String sha,
                                     final String mergeMethod) throws IOException {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/merge");

        Map<String, String> params = new HashMap<>();
        if (commitTitle != null) {
            params.put("commit_title", commitTitle);
        }
        if (commitMessage != null) {
            params.put("commit_message", commitMessage);
        }
        if (sha != null) {
            params.put("sha", sha);
        }
        if (mergeMethod != null) {
            params.put("merge_method", mergeMethod);
        }
        return getClient().put(uri.toString(), params, ExtendedMergeStatus.class);
    }

    public PageIterator<ExtendedCommitComment> pageComments2(final IRepositoryIdProvider repository,
                                                             final int id) {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/comments");

        PagedRequest<ExtendedCommitComment> request = this.createPagedRequest(1, 100);
        request.setUri(uri);
        request.setType((new TypeToken<List<ExtendedCommitComment>>(){}).getType());
        return this.createPageIterator(request);
    }

    public ExtendedCommitComment createComment2(final IRepositoryIdProvider repository,
                                                final int id,
                                                final ExtendedCommitComment comment) throws IOException {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/comments");
        return (ExtendedCommitComment)this.client.post(uri.toString(), comment, ExtendedCommitComment.class);
    }

    public ExtendedCommitComment replyToComment2(final IRepositoryIdProvider repository,
                                                 final int pullRequestId,
                                                 final int commentId,
                                                 final String body) throws IOException {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(pullRequestId);
        uri.append("/comments");
        Map<String, String> params = new HashMap<>();
        params.put("in_reply_to", Integer.toString(commentId));
        params.put("body", body);
        return (ExtendedCommitComment)this.client.post(uri.toString(), params, ExtendedCommitComment.class);
    }

    public ExtendedCommitComment editComment2(final IRepositoryIdProvider repository,
                                              final ExtendedCommitComment comment) throws IOException {
        Objects.requireNonNull(comment, "comment cannot be null");
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append("/comments");
        uri.append('/').append(comment.getId());
        return (ExtendedCommitComment)this.client.post(uri.toString(), comment, ExtendedCommitComment.class);
    }

    public PageIterator<User> pageRequestedReviewers(final IRepositoryIdProvider repository, final int id) {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/requested_reviewers");

        PagedRequest<User> request = this.createPagedRequest(1, 100);
        request.setUri(uri);
        request.setType((new TypeToken<List<User>>(){}).getType());
        return this.createPageIterator(request);
    }

    public PageIterator<Team> pageRequestedTeamReviewers(final IRepositoryIdProvider repository, final int id) {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/requested_reviewers");

        PagedRequest<Team> request = this.createPagedRequest(1, 100);
        request.setUri(uri);
        request.setType((new TypeToken<List<Team>>(){}).getType());
        return this.createPageIterator(request);
    }

    public PageIterator<Review> pageReviews(final IRepositoryIdProvider repository, final int id) {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/reviews");

        PagedRequest<Review> request = this.createPagedRequest(1, 100);
        request.setUri(uri);
        request.setType((new TypeToken<List<Review>>(){}).getType());
        return this.createPageIterator(request);
    }

    public void createReview(final IRepositoryIdProvider repository,
                             final int id,
                             final String commitId,
                             final String event,
                             final String body) throws IOException {
        if (event != null && (event.equals("REQUEST_CHANGES") || event.equals("COMMENT"))) {
            Objects.requireNonNull(body, "body is a required argument when event equals REQUEST_CHANGES or COMMENT");
        }

        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/reviews");

        Map<String, String> params = new HashMap<>();
        if (commitId != null) params.put("commit_id", commitId);
        if (body != null) params.put("body", body);
        if (event != null) params.put("event", event);
        getClient().post(uri.toString(), params, null);
    }

    public void createReviewRequests(final IRepositoryIdProvider repository,
                                final int id,
                                final List<String> reviewers,
                                final List<String> teamReviewers) throws IOException {
        if (Objects.isNull(reviewers) && Objects.isNull(teamReviewers)) {
            throw new IllegalArgumentException("Either reviewers or teamReviewers must be non-null");
        }
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/requested_reviewers");

        Map<String, Object> params = new HashMap<>();
        if (!Objects.isNull(reviewers)) {
            params.put("reviewers", reviewers);
        }
        if (!Objects.isNull(teamReviewers)) {
            params.put("team_reviewers", teamReviewers);
        }
        getClient().post(uri.toString(), params, ExtendedPullRequest.class);
    }

    public void deleteReviewRequests(final IRepositoryIdProvider repository,
                                     final int id,
                                     final List<String> reviewers,
                                     final List<String> teamReviewers) throws IOException {
        if (Objects.isNull(reviewers) && Objects.isNull(teamReviewers)) {
            throw new IllegalArgumentException("Either reviewers or teamReviewers must be non-null");
        }
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/pulls");
        uri.append('/').append(id);
        uri.append("/requested_reviewers");

        Map<String, Object> params = new HashMap<>();
        if (!Objects.isNull(reviewers)) {
            params.put("reviewers", reviewers);
        }
        if (!Objects.isNull(teamReviewers)) {
            params.put("team_reviewers", teamReviewers);
        }
        getClient().delete(uri.toString(), params);
    }

    public void deleteBranch(final IRepositoryIdProvider repository,
        final String branchReference
    ) throws IOException {
        String repoId = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/git");
        uri.append("/refs");
        uri.append("/heads");
        uri.append('/');
        uri.append(branchReference);
        getClient().delete(uri.toString());
    }
}
