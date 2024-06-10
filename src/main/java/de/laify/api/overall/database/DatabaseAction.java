package de.laify.api.overall.database;

import lombok.Getter;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Getter
public class DatabaseAction<T> {

    private final Publisher<T> publisher;

    private boolean anyResult;
    private int operations;
    private ExceptionCallback exceptionCallback;
    private InteractionCallback<T> interactionCallback;
    private AnyResultCompletionCallback anyResultCompletionCallback;
    private CompletionCallback completionCallback;
    private NoResultCallback noResultCallback;

    public DatabaseAction(final Publisher<T> publisher) {
        this.publisher = publisher;
        this.operations = 1;
        this.anyResult = false;
    }

    public DatabaseAction<T> exception(final ExceptionCallback exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
        return this;
    }

    public DatabaseAction<T> noResult(NoResultCallback noResultCallback) {
        this.noResultCallback = noResultCallback;
        return this;
    }

    public DatabaseAction<T> anyResultCompletion(final AnyResultCompletionCallback successfulCompletion) {
        this.anyResultCompletionCallback = successfulCompletion;
        return this;
    }

    public DatabaseAction<T> completion(CompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
        return this;
    }

    public DatabaseAction<T> operations(int operations) {
        this.operations = operations;

        if(this.operations < 1) {
            this.operations = 1;
        }
        return this;
    }

    public void queue(final InteractionCallback<T> interactionCallback) {
        this.interactionCallback = interactionCallback;

        publisher.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(operations);
            }

            @Override
            public void onNext(T t) {
                anyResult = true;
                if(interactionCallback != null){
                    interactionCallback.onInteraction(t);
                }
            }

            @Override
            public void onError(Throwable t) {
                if(exceptionCallback != null) {
                    exceptionCallback.onException(t);
                }
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                if(anyResult) {
                    if(anyResultCompletionCallback != null) {
                        anyResultCompletionCallback.onComplete();
                    }
                } else {
                    if(noResultCallback != null) {
                        noResultCallback.onNoResult();
                    }
                }

                if(completionCallback != null) {
                    completionCallback.onComplete();
                }
            }
        });
    }

    public void queue() {
        queue(null);
    }

    public T now() {
        CompletableFuture<T> future = new CompletableFuture<>();
        publisher.subscribe(new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(T t) {
                anyResult = true;
                future.complete(t);
            }

            @Override
            public void onError(Throwable t) {
                if(exceptionCallback != null) {
                    exceptionCallback.onException(t);
                }

                future.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                if(!future.isDone()) {
                    future.complete(null);
                }
            }
        });

        if(anyResult) {
            if(anyResultCompletionCallback != null) {
                anyResultCompletionCallback.onComplete();
            }
        } else {
            if(noResultCallback != null) {
                noResultCallback.onNoResult();
            }
        }

        if(completionCallback != null) {
            completionCallback.onComplete();
        }

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseAction<Void> allOf(List<DatabaseAction<?>> actions, Runnable runnable) {
        int size = actions.size();
        CountDownLatch latch = new CountDownLatch(size);
        DatabaseAction<Void> allOfAction = new DatabaseAction<>(null);
        for (DatabaseAction<?> action : actions) {
            action.completion(() -> {
                latch.countDown();
                if (latch.getCount() == 0) {
                    runnable.run();
                }
            }).exception(throwable -> {
                if(action.exceptionCallback != null) {
                    action.exceptionCallback.onException(throwable);
                }
            });
            action.queue();
        }
        return allOfAction;
    }

    public interface ExceptionCallback {
        void onException(final Throwable throwable);
    }

    public interface InteractionCallback<T> {
        void onInteraction(T object);
    }

    public interface AnyResultCompletionCallback {
        void onComplete();
    }

    public interface CompletionCallback {
        void onComplete();
    }

    public interface NoResultCallback {
        void onNoResult();
    }

}
