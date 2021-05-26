#!/bin/bash

STACK_NAME=awsbootstrap
REGION=us-east-1
CLI_PROFILE=awsbootstrap

EC2_INSTANCE_TYPE=t2.micro

# Generate a personal access token with repo and admin:repo_hook
#    permissions from https://github.com/settings/tokens
GH_ACCESS_TOKEN=$(cat ~/.github/aws-bootstrap-access-token)
GH_OWNER=$(cat ~/.github/aws-bootstrap-owner)
GH_REPO=$(cat ~/.github/aws-bootstrap-repo)
GH_BRANCH=master

#Deploys static resources
echo -e "\n\n================= Deploying setup.yml==========="
aws cloudformation deploy \
	--region $REGION \
	--profile $CLI_PROFILE \
	--stack-name $STACK_NAME-setup \
	--template-file setup.yml \
	--no-fail-on-empty-changeset \
	--capabilities CAPABILITY_NAMED_IAN \
	--parameter-overrides \
	CodePipelineBucket=$CODEPIPELINE_BUCKET


# Deploy the CloudFormation template
echo -e "\n\n==========Deploying main.yml======="
aws cloudformation deploy \
	--region $REGION \
	--profile $CLI_PROFILE \
	--stack-name $STACK_NAME \
	--template-file main.yml \
	--no-fail-on-empty-changeset \
	--capabilities CAPABILITY_NAMED_IAM \
	--parameter-overrides \
	EC2InstanceType=$EC2_INSTANCE_TYPE \


if [ $? -eq 0 ]; then
   aws cloudformation list-exports \
        --profile awsbootstrap \
	--query "Exports[?Name=='InstanceEndpoint'].Value"
fi
