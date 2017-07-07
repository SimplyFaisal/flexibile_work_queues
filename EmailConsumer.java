import com.rabbitmq.client.*;

import java.io.IOException;

public class EmailConsumer {

    private static final String QUEUE = "EMAIL";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("[x] Received");
                try {
                    doWork(Tutorial.QueueMessage.parseFrom(body));
                } catch(Exception e) {
                    System.out.println(e);
                } finally {
                    System.out.println("[x] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(QUEUE, false, consumer);
    }

    private static void doWork(Tutorial.QueueMessage message) throws Exception {
        switch(message.getType()) {
            case EMAIL_STUDENT_TASK:
                Tutorial.EmailStudentTask emailStudentTask =
                    Tutorial.EmailStudentTask.parseFrom(message.getData());
                String text = String.format(
                    "Hello %s, you have been registered for %s",
                    emailStudentTask.getStudent().getEmail(),
                    emailStudentTask.getCourse().getName());
                System.out.println(text);
                break;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
