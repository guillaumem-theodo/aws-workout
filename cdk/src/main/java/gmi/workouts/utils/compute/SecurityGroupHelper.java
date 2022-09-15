package gmi.workouts.utils.compute;

import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ec2.CfnVPC;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gmi.workouts.utils.TagsHelper.createCommonTags;

public class SecurityGroupHelper {
    
    public enum DefaultPort {
        SSH,
        ICMP,
        HTTP
    }
    
    public static CfnSecurityGroup createSecurityGroup(Construct scope, CfnVPC vpc, String name, DefaultPort... defaultPorts) {
        return CfnSecurityGroup.Builder.create(scope, name)
                .vpcId(vpc.getAttrVpcId())
                .groupName(name)
                .groupDescription("Security Group:" + name)
                .securityGroupEgress(Collections.singletonList(CfnSecurityGroup.EgressProperty.builder()
                        .cidrIp("0.0.0.0/0")
                        .ipProtocol("-1").fromPort(0).toPort(0)
                        .build()
                ))
                .securityGroupIngress(getSecurityGroupIngress(defaultPorts))
                .tags(createCommonTags(name))
                .build();
    }

    private static List<CfnSecurityGroup.IngressProperty> getSecurityGroupIngress(DefaultPort... defaultPorts) {
        ArrayList<CfnSecurityGroup.IngressProperty> ingressProperties = new ArrayList<>();
        for (DefaultPort defaultPort : defaultPorts) {
            if(DefaultPort.SSH.equals(defaultPort)) {
                ingressProperties.add(CfnSecurityGroup.IngressProperty.builder()
                                        .cidrIp("0.0.0.0/0")
                                        .ipProtocol("tcp").fromPort(22).toPort(22)
                                        .build());
            }
            if(DefaultPort.ICMP.equals(defaultPort)) {
                ingressProperties.add(CfnSecurityGroup.IngressProperty.builder()
                                        .cidrIp("0.0.0.0/0")
                                        .ipProtocol("icmp").fromPort(-1).toPort(-1)
                                        .build());
            }
            if(DefaultPort.HTTP.equals(defaultPort)) {
                ingressProperties.add(CfnSecurityGroup.IngressProperty.builder()
                                        .cidrIp("0.0.0.0/0")
                                        .ipProtocol("tcp").fromPort(80).toPort(80)
                                        .build());
            }
        }

        return ingressProperties;
    }
}
