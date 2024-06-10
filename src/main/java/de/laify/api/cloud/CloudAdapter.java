package de.laify.api.cloud;

import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.request.adjust.CreateInstanceRequest;
import de.laify.api.cloud.communication.request.adjust.InstanceModification;
import de.laify.api.cloud.communication.request.adjust.ModifyInstanceRequest;
import de.laify.api.cloud.communication.request.information.SendInstanceInformationRequest;
import de.laify.api.cloud.communication.request.log.LogMessageRequest;
import de.laify.api.cloud.communication.request.minecraft.CloudCommandProcessRequest;
import de.laify.api.cloud.communication.request.minecraft.PlayerConnectInstanceRequest;
import de.laify.api.cloud.communication.request.information.RetrieveInstanceGroupsRequest;
import de.laify.api.cloud.communication.request.information.RetrieveInstanceInformationRequest;
import de.laify.api.cloud.communication.request.information.RetrieveInstanceRequest;
import de.laify.api.cloud.communication.request.status.InstanceStatusChangeRequest;
import de.laify.api.cloud.communication.request.status.InstanceStatusType;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.handler.CloudRequestHandler;
import de.laify.api.cloud.handler.CloudResponseHandler;
import de.laify.api.cloud.instance.Instance;
import de.laify.api.cloud.listener.CloudListener;
import de.laify.api.overall.netty.coder.NettyDecoder;
import de.laify.api.overall.netty.coder.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class CloudAdapter {

    private int cloudPort;
    private String instanceName;
    private Channel channel;

    @Setter
    private CloudListener cloudListener;

    private final EventLoopGroup group;
    private final HashMap<UUID, CloudRequest<?>> openRequests;
    private final ScheduledExecutorService executorService;

    @Getter
    private static CloudAdapter instance;

    private CloudAdapter() {
        instance = this;

        this.group = new NioEventLoopGroup();
        this.openRequests = new HashMap<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        try {
            for(String line : Files.readAllLines(Paths.get("settings.cloud"))) {
                if(line.startsWith("cloud-port: ")) {
                    this.cloudPort = Integer.parseInt(line.replace("cloud-port: ", ""));
                }

                if(line.startsWith("instance-name: ")) {
                    this.instanceName = line.replace("instance-name: ", "");
                }
            }
        } catch (IOException e) {
            System.out.println("unable to find settings.cloud file");
            e.printStackTrace();

            return;
        }

        Bootstrap bootstrap = new Bootstrap();
        CloudAdapter cloudAdapter = this;
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(@NotNull SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyEncoder(), new NettyDecoder(), new CloudResponseHandler(cloudAdapter), new CloudRequestHandler(cloudAdapter));
                    }
                });
        try {
            this.channel = bootstrap.connect("127.0.0.1", cloudPort).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public SendInstanceInformationRequest sendInstanceInformation(final String destinationInstance, final CloudResponse instanceInformation) {
        return new SendInstanceInformationRequest(instanceInformation, destinationInstance);
    }

    public RetrieveInstanceInformationRequest retrieveInstanceInformation(final String instanceName) {
        return new RetrieveInstanceInformationRequest(this.instanceName, instanceName);
    }

    public RetrieveInstanceRequest retrieveInstance(final String instanceName) {
        return new RetrieveInstanceRequest(instanceName);
    }

    public LogMessageRequest logMessage(final String title, final String message, final int embedColor, final String channelId, final boolean instant) {
        return new LogMessageRequest(title, message, embedColor, channelId, instant);
    }

    public RetrieveInstanceGroupsRequest retrieveInstanceGroups(final String... instanceGroups) {
        return new RetrieveInstanceGroupsRequest(instanceGroups);
    }

    public InstanceStatusChangeRequest sendStatusChange(final int port, final InstanceStatusType instanceStatusType) {
        return new InstanceStatusChangeRequest(instanceName, port, instanceStatusType);
    }

    public ModifyInstanceRequest modifyInstance(final String instanceName, final InstanceModification instanceModification) {
        return new ModifyInstanceRequest(instanceName, instanceModification);
    }

    public CreateInstanceRequest createInstance(final String instanceName, final String instanceGroup) {
        return new CreateInstanceRequest(instanceName, instanceGroup);
    }

    public CreateInstanceRequest createInstance(final String instanceGroup) {
        return new CreateInstanceRequest(null, instanceGroup);
    }

    public PlayerConnectInstanceRequest connectPlayer(final String playerName, final String instanceName) {
        return new PlayerConnectInstanceRequest(playerName, instanceName);
    }

    public CloudCommandProcessRequest processCommand(final String[] commandArgs) {
        return new CloudCommandProcessRequest(commandArgs);
    }

    public void disable() {
        try {
            this.channel.close().sync();
            this.group.shutdownGracefully().sync();
            this.executorService.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void queue(final CloudRequest<?> request) {
        if(!this.channel.isActive())return;

        try {
            this.channel.writeAndFlush(request).sync();

            if(request.getResponseHandler() != null) {
                this.openRequests.put(request.getId(), request);
                this.executorService.schedule(() -> {
                    if(this.openRequests.containsKey(request.getId())) {
                        this.openRequests.remove(request.getId());
                        System.out.println("no server response on " + request.getClass().getSimpleName() + " (" + request.getId().toString() + ")");
                    }
                }, request.getTimeoutSeconds(), TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static CloudAdapter init() {
        return new CloudAdapter();
    }

}
